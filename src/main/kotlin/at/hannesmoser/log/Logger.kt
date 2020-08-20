package at.hannesmoser.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

class Logger<in R : Any> : ReadOnlyProperty<R, Logger> {
  override fun getValue(thisRef: R, property: KProperty<*>) =
    getLogger(getClassForLogging(thisRef.javaClass))

  // getClassForLogging returns the javaClass or the enclosingClass if javaClass refers to a companion object.
  private fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
      it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
  }

  private fun getLogger(forClass: Class<*>): Logger =
    LoggerFactory.getLogger(forClass)
}
