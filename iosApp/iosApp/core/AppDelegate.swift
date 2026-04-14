import UIKit
import UserNotifications
// NOTE: Import FirebaseCore and FirebaseMessaging after adding firebase-ios-sdk via SPM
// import FirebaseCore
// import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // TODO: Uncomment after adding Firebase SDK via SPM
        // FirebaseApp.configure()
        // Messaging.messaging().delegate = self

        UNUserNotificationCenter.current().delegate = self

        application.registerForRemoteNotifications()

        return true
    }

    // MARK: - APNs Token

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        // TODO: Uncomment after adding Firebase SDK via SPM
        // Messaging.messaging().apnsToken = deviceToken
    }

    // MARK: - Foreground Notification Display

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        // Show notification as banner even when app is in foreground
        completionHandler([.banner, .sound])
    }

    // MARK: - Notification Tap Handling

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo

        if let type = userInfo["type"] as? String, type == "price_drop",
           let productId = userInfo["productId"] as? String {
            NotificationRouter.shared.navigateToProduct(productId)
        }

        completionHandler()
    }
}

// MARK: - Firebase Messaging Delegate
// TODO: Uncomment after adding Firebase SDK via SPM
// extension AppDelegate: MessagingDelegate {
//     func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
//         guard let token = fcmToken else { return }
//
//         let koinHelper = IOSKoinHelper()
//         let service = koinHelper.getNotificationsService()
//         let profileService = koinHelper.getProfileService()
//
//         Task {
//             let result = try? await profileService.checkLoginStatus()
//             let isLoggedIn = (result as? ResultSuccess<KotlinBoolean>)?.data?.boolValue ?? false
//             try? await service.handleNewToken(token: token, isLoggedIn: isLoggedIn)
//         }
//     }
// }
