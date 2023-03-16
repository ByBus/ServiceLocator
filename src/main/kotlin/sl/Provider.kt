package sl

interface Provider<T> {
    fun provide(serviceLocator: ServiceLocator): T

    class Single<T>(private val instance: T) : Provider<T> {
        override fun provide(serviceLocator: ServiceLocator): T {
            return instance
        }
    }

    class Factory<T>(private val newInstanceProvider: ServiceLocator.() -> T) : Provider<T> {
        override fun provide(serviceLocator: ServiceLocator): T {
            return newInstanceProvider.invoke(serviceLocator)
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
}