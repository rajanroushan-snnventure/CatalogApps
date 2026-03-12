import SwiftUI
import feature_splash
import feature_productlist
import feature_productdetail
import core_ui   // exposes CoilInitializerKt

@main
struct RevestCatalogApp: App {
    
    init() {
        // 1. Initialize Koin DI
        KoinInitializerKt.doInitKoin()

        // 2. Initialize Coil image loader with the Darwin (URLSession) Ktor engine.
        //    This is required on iOS because Coil 3 has no Application context and
        //    cannot auto-discover the network engine the way it does on Android.
        CoilInitializerKt.doInitCoil()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
