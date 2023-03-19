package sl

import kotlin.reflect.KClass
import sl.ServiceLocator.Key as Key

interface ServiceLocator {

    fun <T : Any> get(key: Key): T

    fun add(key: Key, provider: Provider<*>)

    class Base(
        vararg providers: Pair<Key, Provider<*>>,
    ) : ServiceLocator {
        private val registry = providers.toMap(HashMap())

        override fun <T : Any> get(key: Key): T {
            return registry.getValue(key).provide(this) as T
        }

        override fun add(key: Key, provider: Provider<*>) {
            registry[key] = provider
        }
    }

    class Key(private val type: KClass<*>, private val named: String = "") {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Key
            if (type != other.type || named != other.named) return false
            return true
        }

        override fun hashCode(): Int = 31 * type.hashCode() + named.hashCode()
    }
}

inline fun <reified T : Any> ServiceLocator.get(named: String = ""): T {
    return this.get(Key(T::class, named))
}

infix fun <C : KClass<*>, T : Any> C.from(provider: Provider<T>): Pair<Key, Provider<T>> =
    Pair(named(""), provider)
infix fun <C : KClass<*>> C.named(name: String) = Key(this, name)
infix fun <T : Any> Key.from(provider: Provider<T>) = Pair(this, provider)
fun <C : KClass<*>> C.key(named: String = "") = Key(this, named)
