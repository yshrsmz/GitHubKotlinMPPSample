import UIKit
import data

class ViewController: UIViewController {
    
    let repository = GitHubRepository()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        
        repository.fetchUser(login: "yshrsmz") { (user, errors) -> KotlinUnit in
            if (user != nil) {
                NSLog("user: \(user)")
            } else {
                NSLog("errors: \(errors)")
            }
            return KotlinUnit()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBOutlet weak var label: UILabel!
}
