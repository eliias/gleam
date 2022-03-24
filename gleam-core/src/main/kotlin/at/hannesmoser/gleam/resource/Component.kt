package at.hannesmoser.gleam.resource

import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools

interface Component {
  fun getContainer(): Koin = WorkerApplication.getInstance().koin
}

/**
 * Get instance from Koin
 * @param qualifier
 * @param parameters
 */
inline fun <reified T : Any> Component.get(
  qualifier: Qualifier? = null,
  noinline parameters: ParametersDefinition? = null
): T {
  return if (this is KoinScopeComponent) {
    scope.get(qualifier, parameters)
  } else getContainer().get(qualifier, parameters)
}

/**
 * Lazy inject instance from Koin
 * @param qualifier
 * @param mode - LazyThreadSafetyMode
 * @param parameters
 */
inline fun <reified T : Any> Component.inject(
  qualifier: Qualifier? = null,
  mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
  noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy(mode) { get(qualifier, parameters) }
