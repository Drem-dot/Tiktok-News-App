
```markdown
# 🚀 NewsTok: The Intelligent News Feed

NewsTok is a native Android news reader application designed to revolutionize how you consume news. It features a modern, TikTok-style vertical feed and leverages Google's Gemini AI to provide concise, AI-generated summaries and a personalized content stream.

<div align="center">
  <!-- TODO: Add a GIF or Screenshot of the app here -->
  <img src="your_screenshot_url_here.png" width="250">
</div>

## ✨ Core Features

- **Dynamic Vertical Feed:** An immersive, full-screen vertical feed built with `ViewPager2` for seamless article browsing.
- **AI-Powered Summaries:** Before you even read, get a one-sentence, catchy summary of each article, generated on-the-fly by the **Google Gemini 1.5 Flash model**.
- **Content-Based Recommendation Engine:** The app learns from your behavior!
  - When you **Like ❤️** an article, the app analyzes its title, extracts keywords, and increases their relevance score in a local Room database.
  - The main feed is then algorithmically re-ranked to prioritize content matching your highest-scored keywords.
- **Save & Read Later:** Bookmark interesting articles with a single tap. All saved articles are neatly organized in a separate library screen for easy access.
- **Seamless In-App Reading:** Open the original article in a fully integrated `WebView`, ensuring a smooth reading experience without ever leaving the app.

## 🛠️ Technology & Architecture

- **Language:** **Java**
- **Architecture:** Client-side architecture with a focus on separating concerns.
  - **UI Layer:** XML with Material Design Components.
  - **Data Layer:** Repository pattern principles using Room Database for local persistence and Retrofit for remote data fetching from NewsAPI.org.
- **Key Libraries:**
  - `Retrofit & Gson`: For type-safe HTTP requests to the NewsAPI.
  - `Room Database`: For robust, local storage of user interactions (likes, saves) and keyword scores.
  - `Google Generative AI SDK`: To connect to and stream responses from the Gemini API.
  - `ViewPager2`: For the core vertical swiping functionality.
  - `Material Design Components`: For a modern and consistent UI.

## ⚙️ Setup & Configuration

To run this project, you will need API keys from both NewsAPI and Google AI Studio.

1.  Clone the repository.
2.  Get your API keys:
    - **NewsAPI:** [newsapi.org](https://newsapi.org/)
    - **Gemini API:** [aistudio.google.com](https://aistudio.google.com/)
3.  Open the project in Android Studio and navigate to `MainActivity.java`.
4.  Find and replace the placeholder strings with your actual keys:

    ```java
    // In MainActivity.java
    String apiKey = "YOUR_NEWSAPI_KEY_HERE";
    String geminiApiKey = "YOUR_GEMINI_API_KEY_HERE";
    ```
5.  Build and run the app.
