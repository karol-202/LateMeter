package pl.karol202.latemeter.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

fun <T> property(propertySupplier: () -> KMutableProperty0<T>) = PropertyDelegate(propertySupplier)

class PropertyDelegate<T>(val propertySupplier: () -> KMutableProperty0<T>) : ReadWriteProperty<Any, T>
{
	override fun getValue(thisRef: Any, property: KProperty<*>) = propertySupplier().get()

	override fun setValue(thisRef: Any, property: KProperty<*>, value: T)
	{
		propertySupplier().set(value)
	}
}