interface Storage {
    val id: Int
}

class MyStorage(override val id: Int) : Storage

class OtherStorage(override val id: Int): Storage