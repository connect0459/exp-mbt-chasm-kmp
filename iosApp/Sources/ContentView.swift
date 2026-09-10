import Shared
import SwiftUI

struct ContentView: View {
  @State private var resultText = "Running..."

  var body: some View {
    Text(resultText)
      .padding()
      .font(.system(.body, design: .monospaced))
      .task {
        let guestService = GuestServiceFactoryKt.createGuestService()
        let value = guestService.increment(p0: 41)
        resultText = "increment(41) = \(value)"
      }
  }
}
