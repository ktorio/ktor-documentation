import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var viewModel = ViewModel()

    var body: some View {
        Text(viewModel.text)
    }
}

extension ContentView {
    @MainActor
    class ViewModel: ObservableObject {
        @Published var text = "Loading..."
        init() {
            Greeting().greet { greeting, error in
                if let greeting = greeting {
                    self.text = greeting
                } else {
                    self.text = error?.localizedDescription ?? "error"
                }
            }
        }
    }
}