// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.application.newapi

public class FeatureNotInstalledException(private val featureName: String): Exception() {
    override val message: String?
        get() = "Feature $featureName is not installed but required"
}
