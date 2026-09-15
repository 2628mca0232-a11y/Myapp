# 📱 EMIease – Smart Multiple Loan & EMI Manager (Android)

> **One Dashboard. Every Loan. Zero EMI Confusion.**

**EMIease** is a modern, native Android application built with **Jetpack Compose** and **Material 3**. It provides users with a comprehensive financial dashboard to consolidate, track, calculate, and manage multiple loans, credit card EMIs, education borrowings, and vehicle financing in real time.

---

## ✨ Features

- 📊 **Smart Dashboard**: Real-time KPI summary cards displaying Total Active Loans, Monthly EMI Commitment, Outstanding Liability, and Repaid Principal Percentage alongside a visual **Debt Health Meter**.
- 💳 **Complete Loan Accounts Management**: Search and filter loans by category (*Personal, Home, Education, Vehicle, Credit Card EMI, Gold Loan, Business Loan*). View month-by-month amortization schedules and payment history.
- 📅 **Interactive Payment Calendar**: Visual monthly calendar highlighting due dates with color-coded status badges (*Paid*, *Due Today*, *Due Soon*, *Overdue*).
- 🧮 **Smart EMI & Prepayment Calculator**: Interactive sliders to simulate principal amounts, interest rates, and tenures. Features a **Prepayment Savings Simulator** to calculate months and interest saved via lump-sum payments.
- 📈 **Debt Analytics & Insights**: Visual progress bars and metrics breaking down outstanding debt by loan category and lender/bank.
- 🔔 **Alerts & Reminders Center**: Categorized payment alerts ensuring you never miss an EMI due date.
- ⚙️ **Data Backup & Preferences**: Local offline `SharedPreferences` JSON persistence, multi-currency support (*₹ INR, $ USD, € EUR, £ GBP*), and JSON export/import data backup.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel) with `StateFlow` reactive streams
- **Persistence**: Android `SharedPreferences` with custom JSON serialization
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`)
- **Compatibility**: Android 7.0+ (Min SDK 24, Target SDK 37)

---

## 🚀 Getting Started

### Prerequisites
- [Android Studio Ladybug / Koala](https://developer.android.com/studio) or newer
- Android SDK 37
- JDK 11 or higher

### Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/2628mca0232-a11y/Myapp.git
   cd Myapp
   ```

2. **Open in Android Studio:**
   - Open Android Studio $\rightarrow$ **Open An Existing Project** $\rightarrow$ Select the cloned `Myapp` folder.

3. **Build & Run:**
   - Connect an Android phone via USB debugging or start an Android Emulator.
   - Click the **Run (▶)** button in Android Studio (or press `Shift + F10`).

---

## 📦 APK Download

To generate the standalone `.apk` file:
```bash
./gradlew app:assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

This project is an academic prototype for Design Thinking & Entrepreneurship. Distributed under the MIT License.
