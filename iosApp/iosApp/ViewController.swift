import UIKit
import data

class ViewController: UIViewController {
    
    let repository = DataModuleKt.getGitHubRepository()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        let login = "yshrsmz"
        
        let query = repository.observeUser(login: login)
        
        let userNotifier = UserDataNotifier(query: query) { (user :User?) -> KotlinUnit in
            if (user == nil) {
                NSLog("user is nil")
            } else {
                NSLog("user: \(user)")
            }
            return KotlinUnit()
        }
        
        repository.fetchUser(login: login)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBOutlet weak var label: UILabel!
}
