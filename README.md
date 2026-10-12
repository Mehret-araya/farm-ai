# FarmAI — AI-Powered Agriculture Assistant

An Android app that helps Ethiopian farmers diagnose crop diseases, manage farm plots, and get AI-powered agronomic advice in English and Amharic.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-purple)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Overview

FarmAI is an offline-first Android application built for Ethiopian farmers. It combines on-device data persistence with cloud AI to provide practical agronomic guidance — including crop disease diagnosis, farm plot tracking, regional weather forecasts, and a bilingual AI chatbot.

## Features

- **AI Crop Disease Scanner** — photograph a leaf, get disease diagnosis with treatment recommendations
- **Dr. Almaz AI Chatbot** — conversational agronomist for crop questions, in English and Amharic
- **Farm Plot Management** — track crops through growth stages with field notes
- **Regional Agro-Weather Forecast** — 5-day projections for Ethiopian farming zones (Arsi, Jimma, Debre Zeit)
- **Crop Care Guides** — agronomic profiles for 9 Ethiopian crops (Teff, Wheat, Maize, Coffee, Barley, Sorghum, Tomato, Potato, Chickpea) sourced from EIAR guidelines
- **Field Journal** — save advice, diagnostics, and observations to a persistent local database
- **Bilingual Interface** — full English + Amharic support
- **Offline-First** — core features work without internet; AI features fall back to a local rule engine

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM (ViewModel + Repository) |
| Local Database | Room SQLite |
| AI | Gemini 3.5 Flash API with offline rule-engine fallback |
| Async | Kotlin Coroutines + StateFlow |
| Camera | Native Android Camera API + Photo Picker |
| Build | Gradle Kotlin DSL |

## Architecture

The app follows MVVM (Model-View-ViewModel) with a repository layer:

- **UI Layer** — Jetpack Compose screens with enum-based navigation
- **ViewModel Layer** — FarmViewModel exposes state via StateFlow
- **Repository Layer** — FarmRepository abstracts Room DAO and Gemini API calls
- **Data Layer** — Room SQLite for local persistence, Retrofit for Gemini API

Offline-first design: The AI Disease Scanner and Chat features call the Gemini API when a key is present. If offline or no key is configured, they fall back to a local rule-based engine.

## Project Structure
app/
├── src/main/java/com/example/
│ ├── data/
│ │ ├── local/
│ │ └── repository/
│ ├── ui/
│ │ ├── screens/
│ │ ├── components/
│ │ └── theme/
│ └── viewmodel/
└── src/main/res/


## Getting Started

### Prerequisites
- Android Studio (latest stable)
- JDK 17+
- Android SDK 24+
- Gemini API key (optional — app works offline without it)

### Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/Mehret-araya/farm-ai.git
   cd farm-ai

   Add your Gemini API key (optional):

Copy .env.example to .env

Add: GEMINI_API_KEY=your_key_here

Build and run:

bash
./gradlew assembleDebug
Known Limitations
Weather data is preloaded, not fetched from a live weather API

Crop disease diagnosis is a decision-support tool, not a replacement for professional advice

AI features fall back to a rule-based engine when offline

No automated tests yet

What I'd Build Next
Live weather API integration

On-device ML model for offline disease detection

Automated tests for diagnostic and chat flows

Author
Mehret Araya Gebreslassie — Fullstack Developer

GitHub: https://github.com/Mehret-araya

LinkedIn: https://www.linkedin.com/in/mehret-araya


License
MIT

text

**Note:** I removed the emojis this time — they were causing the corruption. The README will still look professional without them.

---

## ⚠️ Why Notepad Corrupted It

When you copy text with Unicode characters (emojis like 🌱, 💬, and em-dashes like —) and paste it into **Notepad, Word, or certain chat apps**, they re-encode the characters in a different format (like Windows-1252 or ANSI) that mangles them into gibberish.

**The safe path:** copy from your **browser → paste directly into GitHub's editor**. Never go through Notepad.

---

## ✅ Do This Now

1. **Go back to the GitHub README editor tab**
2. **`Ctrl + A` → `Delete`** to clear the corrupted text
3. **Paste the corrected README above** (no emojis this time)
4. **Scroll down** → commit message: `docs: add professional README`
5. **Click "Commit changes"**

---

## 🎯 Before You Commit — One Last Check

After pasting, look at the top of the editor. Does it say:
FarmAI — AI-Powered Agriculture Assistant
text

**If yes** → safe to commit. ✅
**If it still shows `рҹҢұ` or `вҖ”`** → the corruption happened again. Do not commit.

---

## 💬 Tell Me After

Once committed, tell me:
1. Did the README render correctly on the GitHub page?
2. Screenshot it if you're unsure

**And have you sent Hermela the message yet?** If not — that's still the most urgent thing. 🚀
