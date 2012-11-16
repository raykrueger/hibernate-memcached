/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached.strategy;

import com.googlecode.hibernate.memcached.region.AbstractMemcachedRegion;
import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kcarlson
 */
public class AbstractReadWriteMemcachedAccessStrategy <T extends AbstractMemcachedRegion> extends AbstractMemcachedAccessStrategy<T> {
 
   private final Logger log = LoggerFactory.getLogger(AbstractReadWriteMemcachedAccessStrategy.class);
     
    private final UUID uuid = UUID.randomUUID();
    private final AtomicLong nextLockId = new AtomicLong();
     
    private final Comparator versionComparator;
 
    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public AbstractReadWriteMemcachedAccessStrategy(T region, Settings settings, CacheDataDescription cacheDataDescription) {
        super(region, settings);
        this.versionComparator = cacheDataDescription.getVersionComparator();
    }
 
    /**
     * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
     * after the start of this transaction.
     *
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#get(java.lang.Object, long)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#get(java.lang.Object, long)
     */
    public final Object get(Object key, long txTimestamp) throws CacheException {
        readLockIfNeeded(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);
 
            boolean readable = item != null && item.isReadable(txTimestamp);
            if (readable) {
                return item.getValue();
            } else {
                return null;
            }
        } finally {
            readUnlockIfNeeded(key);
        }
    }
 
    /**
     * Returns <code>false</code> and fails to put the value if there is an existing un-writeable item mapped to this
     * key.
     *
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean) 
     */
    @Override
    public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException {
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);
            boolean writeable = item == null || item.isWriteable(txTimestamp, version, versionComparator);
            if (writeable) {
                region.getCache().put(key, new Item(value, version, region.nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region.getCache().unlock(key);
        }
    }
 
    /**
     * Soft-lock a cache item.
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#lockItem(java.lang.Object, java.lang.Object)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#lockItem(java.lang.Object, java.lang.Object) 
     */
    public final SoftLock lockItem(Object key, Object version) throws CacheException {
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);
            long timeout = region.nextTimestamp() + region.getTimeout();
            final Lock lock = (item == null) ? new Lock(timeout, uuid, nextLockId(), version) : item.lock(timeout, uuid, nextLockId());
            region.getCache().put(key, lock);
            return lock;
        } finally {
            region.getCache().unlock(key);
        }
    }
 
    /**
     * Soft-unlock a cache item.
     *
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#unlockItem(java.lang.Object, org.hibernate.cache.access.SoftLock)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#unlockItem(java.lang.Object, org.hibernate.cache.access.SoftLock) 
     */
    public final void unlockItem(Object key, SoftLock lock) throws CacheException {
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);
 
            if ((item != null) && item.isUnlockable(lock)) {
                decrementLock(key, (Lock) item);
            } else {
                handleLockExpiry(key, item);
            }
        } finally {
            region.getCache().unlock(key);
        }
    }
 
    private long nextLockId() {
        return nextLockId.getAndIncrement();
    }
 
    /**
     * Unlock and re-put the given key, lock combination.
     */
    protected void decrementLock(Object key, Lock lock) {
        lock.unlock(region.nextTimestamp());
        region.getCache().put(key, lock);
    }
 
    /**
     * Handle the timeout of a previous lock mapped to this key
     */
    protected void handleLockExpiry(Object key, Lockable lock) {
        log.warn("Cache " + region.getName() + " Key " + key + " Lockable : " + lock + "\n"
                + "A soft-locked cache entry was expired by the underlying Memcache. "
                + "If this happens regularly you should consider increasing the cache timeouts and/or capacity limits");
        long ts = region.nextTimestamp() + region.getTimeout();
        // create new lock that times out immediately
        Lock newLock = new Lock(ts, uuid, nextLockId.getAndIncrement(), null);
        newLock.unlock(ts);
        region.getCache().put(key, newLock);
    }
 
    /**
     * Read lock the entry for the given key if internal cache locks will not provide correct exclusion.
     */
    private void readLockIfNeeded(Object key) {
        region.getCache().lock(key);
    }
 
    /**
     * Read unlock the entry for the given key if internal cache locks will not provide correct exclusion.
     */
    private void readUnlockIfNeeded(Object key) {
        region.getCache().unlock(key);
    }
 
    /**
     * Interface type implemented by all wrapper objects in the cache.
     */
    protected static interface Lockable {
 
        /**
         * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
         */
        public boolean isReadable(long txTimestamp);
 
        /**
         * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
         * transaction started at the given time.
         */
        public boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
 
        /**
         * Returns the enclosed value.
         */
        public Object getValue();
 
        /**
         * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
         */
        public boolean isUnlockable(SoftLock lock);
 
        /**
         * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
         * time.  The returned Lock object can be used to unlock the entry in the future.
         */
        public Lock lock(long timeout, UUID uuid, long lockId);
    }
 
    /**
     * Wrapper type representing unlocked items.
     */
    protected final static class Item implements Serializable, Lockable {
 
        private static final long serialVersionUID = 1L;
        private final Object value;
        private final Object version;
        private final long timestamp;
 
        /**
         * Creates an unlocked item wrapping the given value with a version and creation timestamp.
         */
        Item(Object value, Object version, long timestamp) {
            this.value = value;
            this.version = version;
            this.timestamp = timestamp;
        }
 
        /**
         * {@inheritDoc}
         */
        public boolean isReadable(long txTimestamp) {
            return txTimestamp > timestamp;
        }
 
        /**
         * {@inheritDoc}
         */
        public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
            return version != null && versionComparator.compare(version, newVersion) < 0;
        }
 
        /**
         * {@inheritDoc}
         */
        public Object getValue() {
            return value;
        }
 
        /**
         * {@inheritDoc}
         */
        public boolean isUnlockable(SoftLock lock) {
            return false;
        }
 
        /**
         * {@inheritDoc}
         */
        public Lock lock(long timeout, UUID uuid, long lockId) {
            return new Lock(timeout, uuid, lockId, version);
        }
    }
 
    /**
     * Wrapper type representing locked items.
     */
    protected final static class Lock implements Serializable, Lockable, SoftLock {
 
        private static final long serialVersionUID = 2L;
 
        private final UUID sourceUuid;
        private final long lockId;
        private final Object version;
 
        private long timeout;
        private boolean concurrent;
        private int multiplicity = 1;
        private long unlockTimestamp;
 
        /**
         * Creates a locked item with the given identifiers and object version.
         */
        Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
            this.timeout = timeout;
            this.lockId = lockId;
            this.version = version;
            this.sourceUuid = sourceUuid;
        }
 
        /**
         * {@inheritDoc}
         */
        public boolean isReadable(long txTimestamp) {
            return false;
        }
 
        /**
         * {@inheritDoc}
         */
        public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
            if (txTimestamp > timeout) {
                // if timedout then allow write
                return true;
            }
            if (multiplicity > 0) {
                // if still locked then disallow write
                return false;
            }
            return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(version, newVersion) < 0;
        }
 
        /**
         * {@inheritDoc}
         */
        public Object getValue() {
            return null;
        }
 
        /**
         * {@inheritDoc}
         */
        public boolean isUnlockable(SoftLock lock) {
            return equals(lock);
        }
 
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Lock) {
                return (lockId == ((Lock) o).lockId) && sourceUuid.equals(((Lock) o).sourceUuid);
            } else {
                return false;
            }
        }
 
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int hash = (sourceUuid != null ? sourceUuid.hashCode() : 0);
            int temp = (int) lockId;
            for (int i = 1; i < Long.SIZE / Integer.SIZE; i++) {
                temp ^= (lockId >>> (i * Integer.SIZE));
            }
            return hash + temp;
        }
 
        /**
         * Returns true if this Lock has been concurrently locked by more than one transaction.
         */
        public boolean wasLockedConcurrently() {
            return concurrent;
        }
 
        /**
         * {@inheritDoc}
         */
        public Lock lock(long timeout, UUID uuid, long lockId) {
            concurrent = true;
            multiplicity++;
            this.timeout = timeout;
            return this;
        }
 
        /**
         * Unlocks this Lock, and timestamps the unlock event.
         */
        public void unlock(long timestamp) {
            if (--multiplicity == 0) {
                unlockTimestamp = timestamp;
            }
        }
 
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId);
            return sb.toString();
        }
    }
}