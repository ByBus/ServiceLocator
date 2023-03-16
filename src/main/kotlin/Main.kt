import sl.*

fun main() {
    val serviceLocator = ServiceLocator.Base(
        Storage::class named "other" from Provider.Single(OtherStorage(100)),
        Storage::class from Provider.Single(MyStorage(5)),
        StorageRepository::class from Provider.Factory { StorageRepository(get(), get("other"),2) },
        MyStorage::class from Provider.Lazy { MyStorage(13) },
    )

    val storage1 = serviceLocator.get<Storage>()
    println("Created Storage1 $storage1 with id ${storage1.id}")
    val storage2: Storage = serviceLocator.get()
    println("Created Storage2 $storage2 with id ${storage2.id}")

    val repository1: StorageRepository = serviceLocator.get()
    println("Created StorageRepository1 $repository1 with dependency ${repository1.storage} and ${repository1.otherStorage}")
    val repository2: StorageRepository = serviceLocator.get()
    println("Created StorageRepository2 $repository2 with dependency ${repository2.storage} and ${repository1.otherStorage}")

    val myStorage1: MyStorage = serviceLocator.get()
    println("Created MyStorage1 $myStorage1 with id ${myStorage1.id}")
    val myStorage2: MyStorage = serviceLocator.get()
    println("Created MyStorage2 $myStorage2 with id ${myStorage2.id}")

    val otherStorage1: Storage = serviceLocator.get(named = "other")
    println("Created OtherStorage1 $otherStorage1 with id ${otherStorage1.id}")
    val otherStorage2: Storage = serviceLocator.get("other")
    println("Created OtherStorage2 $otherStorage2 with id ${otherStorage2.id}")
}