package sl

import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

interface Provider<T> {
    fun provide(serviceLocator: ServiceLocator): T

    class Single<T>(private val instance: T) : Provider<T> {
        override fun provide(serviceLocator: ServiceLocator): T {
            return instance
        }
    }

    class Factory<T>(private val factory: ServiceLocator.() -> T) : Provider<T> {
        override fun provide(serviceLocator: ServiceLocator): T {
            return factory.invoke(serviceLocator)
        }
    }

    class Lazy<T>(private val instanceProvider: ServiceLocator.() -> T) : Provider<T> {
        @Volatile
        private var sl: ServiceLocator? = null
        private val instance by lazy { instanceProvider.invoke(sl!!) }

        override fun provide(serviceLocator: ServiceLocator): T {
            if (sl == null) {
                synchronized(this) {
                    if (sl == null) {
                        sl = serviceLocator
                    }
                }
            }
            return instance
        }
    }

    class LazyAtomic<T>(private val instanceProvider: ServiceLocator.() -> T) : Provider<T> {
        private val initialized = AtomicBoolean(false)
        private var sl: ServiceLocator? = null
        private val instance by lazy { instanceProvider.invoke(sl!!) }

        override fun provide(serviceLocator: ServiceLocator): T {
            return if (initialized.compareAndSet(false, true)) {
                sl = serviceLocator
                instance.also { sl = null }
            } else {
                instance
            }
        }
    }

    class LazyWeak<T>(private val instanceProvider: ServiceLocator.() -> T) : Provider<T> {
        private var instance: WeakReference<T>? = null
        override fun provide(serviceLocator: ServiceLocator): T {
            return synchronized(this) {
                instance?.get() ?: instanceProvider.invoke(serviceLocator)
                    .also {
                        instance = WeakReference(it)
                    }
            }
        }
    }
}