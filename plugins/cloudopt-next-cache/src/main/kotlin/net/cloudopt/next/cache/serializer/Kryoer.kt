package net.cloudopt.next.cache.serializer

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import de.javakaffee.kryoserializers.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.InvocationHandler
import java.util.*

object Kryoer {

    private const val DEFAULT_ENCODING = "UTF-8"

    /**
     * Because Kryo is not thread safe and constructing and configuring a Kryo instance is relatively expensive,
     * in a multithreaded environment ThreadLocal or pooling might be considered.
     */
    private val kryoLocal: ThreadLocal<Kryo> = object : ThreadLocal<Kryo>() {
        override fun initialValue(): Kryo {
            val kryo = Kryo()

            /**
             * If true, each appearance of an object in the graph after the first is stored as an integer ordinal.
             */
            kryo.references = true

            /* If false, when an unregistered class is encountered, its fully qualified class name will be serialized and the
            * {@link #addDefaultSerializer(Class, Class) default serializer} for the class used to serialize the object. Subsequent
            * appearances of the class within the same object graph are serialized as an int id.
             */
            kryo.isRegistrationRequired = false

            /**
             *  register the custom serializers at the kryo instance.
             */
            kryo.register(Arrays.asList("")::class.java, ArraysAsListSerializer())
            kryo.register(Collections.EMPTY_LIST.javaClass, CollectionsEmptyListSerializer())
            kryo.register(Collections.EMPTY_MAP.javaClass, CollectionsEmptyMapSerializer())
            kryo.register(Collections.EMPTY_SET.javaClass, CollectionsEmptySetSerializer())
            kryo.register(listOf("").javaClass, CollectionsSingletonListSerializer())
            kryo.register(setOf("").javaClass, CollectionsSingletonSetSerializer())
            kryo.register(Collections.singletonMap("", "").javaClass, CollectionsSingletonMapSerializer())
            kryo.register(GregorianCalendar::class.java, GregorianCalendarSerializer())
            kryo.register(InvocationHandler::class.java, JdkProxySerializer())
            UnmodifiableCollectionsSerializer.registerSerializers(kryo)
            SynchronizedCollectionsSerializer.registerSerializers(kryo)
            return kryo
        }
    }

    /**
     * Get the Kryo instance of the current thread.
     * @return the Kryo instance of the current thread
     */
    private val instance: Kryo
        get() = kryoLocal.get()

    /**
     * Serialize objects [and types] to byte arrays.
     * @param obj any object
     * @param <T> the type of object
     * @return byte arrays
    </T> */
    fun <T> writeToByteArray(obj: T): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val output = Output(byteArrayOutputStream)
        val kryo = instance
        kryo.writeClassAndObject(output, obj)
        output.flush()
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Deserialize byte arrays to objects.
     * @param byteArray writeToByteArray
     * @param <T>
     * @return object
    </T> */
    fun <T> readFromByteArray(byteArray: ByteArray?): T {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val input = Input(byteArrayInputStream)
        val kryo = instance
        return kryo.readClassAndObject(input) as T
    }

    /**
     * Serializing objects to byte arrays.
     *
     * @param obj any object
     * @param <T>
     * @return byte arrays
    </T> */
    fun <T> writeObjectToByteArray(obj: T): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val output = Output(byteArrayOutputStream)
        val kryo = instance
        kryo.writeObject(output, obj)
        output.flush()
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Deserialize byte arrays to objects
     * @param byteArray writeToByteArray byte arrays after serialization
     * @param clazz class of the original object
     * @param <T> type of the original object
     * @return 原对象
    </T> */
    fun <T> readObjectFromByteArray(byteArray: ByteArray?, clazz: Class<T>?): T {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val input = Input(byteArrayInputStream)
        val kryo = instance
        return kryo.readObject(input, clazz)
    }
}