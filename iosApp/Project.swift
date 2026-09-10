import ProjectDescription

let project = Project(
  name: "MbtChasmKmpHost",
  organizationName: "dev.connect0459",
  targets: [
    .target(
      name: "MbtChasmKmpHost",
      destinations: .iOS,
      product: .app,
      bundleId: "dev.connect0459.MbtChasmKmpHost",
      deploymentTargets: .iOS("18.0"),
      infoPlist: .extendingDefault(
        with: [
          "UILaunchScreen": [:],
          "UIApplicationSceneManifest": [
            "UIApplicationSupportsMultipleScenes": false
          ],
        ]
      ),
      sources: ["Sources/**"],
      scripts: [
        // Direct integration of the shared KMP module's iOS framework, per
        // https://kotlinlang.org/docs/multiplatform-direct-integration.html
        // — the officially documented non-CocoaPods approach. Requires
        // `binaries.framework { baseName = "Shared" }` on shared/'s
        // iosSimulatorArm64 target (already set), and User Script
        // Sandboxing disabled below (the task fails under sandboxing).
        .pre(
          script: """
            if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \\"YES\\""
                exit 0
            fi
            cd "$SRCROOT/.."
            ./gradlew :shared:embedAndSignAppleFrameworkForXcode
            """,
          name: "Embed Shared.framework",
          basedOnDependencyAnalysis: false
        )
      ],
      settings: .settings(base: ["ENABLE_USER_SCRIPT_SANDBOXING": "NO"])
    )
  ]
)
