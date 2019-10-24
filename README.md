# XKCD Comic Reader (unofficial)
Fan made [XKCD](https://xkcd.com/) comic reader using the official [JSON interface](https://xkcd.com/json.html).

## Architecture

- The app is entirely written in [Kotlin](https://kotlinlang.org/).
- [Dagger 2 Android](https://github.com/google/dagger) is utilised for Dependency Injection.
- There is a single Activity which swaps Fragments inside a Non-Swipeable ViewPager (custom view) using a [Bottom Navigation View](https://developer.android.com/reference/android/support/design/widget/BottomNavigationView).
- Remote Data: Network calls are handled by [Retrofit 2](https://github.com/square/retrofit).
- Local Data: There is a local [Room Database](https://developer.android.com/topic/libraries/architecture/room) for persisting fetched comics and recording user favourites. There is also an extra table created for handling **Paging**.
- Data loading and writing is abstracted behind a **ComicsRepo**.
- Business logic is handled by reusable **Interactors** who also handle threading via injected schedulers.
- Each Fragment has its own [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) that emits actions via LiveData.
- View Binding is in turn handled by each Fragment's dedicated **View Binder**.
- Swiping cards is handled by the latest version of [Card Stack View](https://github.com/yuyakaido/CardStackView) which uses a recycler adapter.
- Favourites are toggled using a custom **FavouriteImageView**.

## Media

![swipe](media/swipe.gif "Swiping images")

![favourites](media/favourite.gif "Adding to Favourites")

![search](media/search.gif "Searching for a comic")

![image preview](media/image_preview.gif "Full screen image preview")

## Dependencies
* [Kotlin](https://developer.android.com/kotlin/)
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
* [AndroidX](https://developer.android.com/jetpack/androidx/)
* [Paging Library](https://developer.android.com/topic/libraries/architecture/paging)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Card Stack View](https://github.com/yuyakaido/CardStackView)
* [Dagger 2](https://github.com/google/dagger)
* [Retrofit 2](https://github.com/square/retrofit)
* [Fresco](https://github.com/facebook/fresco)
* [Kotpref](https://github.com/chibatching/Kotpref)
* [Toasty](https://github.com/GrenderG/Toasty)
* [Lovely Dialog](https://github.com/yarolegovich/LovelyDialog)
* [Timber](https://github.com/JakeWharton/timber)
* [ktlint](https://github.com/shyiko/ktlint)

## Disclaimer
I do not own or affiliate myself with [XKCD](https://xkcd.com/), but I love the comic. Hope you enjoy the app as much as I do.

## Releases
The app will not go into Play Store as this is not endorsed and unofficial. Sill, if you want a copy of the reader, head over to the [releases](https://github.com/MikeFot/android--xkcd-reader/releases) page and download an APK.



