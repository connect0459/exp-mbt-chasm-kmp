import Shared
import SwiftUI

struct ContentView: View {
  @State private var resultText = "Running..."

  var body: some View {
    ScrollView {
      Text(resultText)
        .padding()
        .multilineTextAlignment(.leading)
        .font(.system(.body, design: .monospaced))
    }
    .task {
      let guestService = GuestServiceFactoryKt.createGuestService()
      let value = guestService.increment(p0: 41)

      let iterations: Int32 = 100_000
      let incrementBench = GuestBenchmarkKt.benchmarkIncrement(iterations: iterations)
      let movePointBench = GuestBenchmarkKt.benchmarkMovePoint(iterations: iterations)

      resultText = """
        increment(41) = \(value)

        \(GuestBenchmarkKt.formatGuestBenchmarkResult(result: incrementBench))

        \(GuestBenchmarkKt.formatGuestBenchmarkResult(result: movePointBench))
        """
    }
  }
}
