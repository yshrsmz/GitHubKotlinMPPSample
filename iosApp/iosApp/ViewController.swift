import UIKit
import data

class ViewController: UIViewController {
    
    let repository = DataModuleKt.getGitHubRepository()
    
    lazy var repoNotifier = UserRepositoryDataNotifier { (repos) -> KotlinUnit in
        self.onUserRepoUpdate(repos: repos)
        return KotlinUnit()
    }
    
    lazy var userNotifier = UserDataNotifier { (user) -> KotlinUnit in
        self.onUserUpdate(user: user)
        return KotlinUnit()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        let login = "yshrsmz"
        
        let query = repository.observeUser(login: login)
        userNotifier.updateQuery(newQuery: query)
        
        repository.fetchUser(login: login)
    }
    
    func onUserUpdate(user: User?) {
        if (user == nil) {
            NSLog("user is nil")
        } else {
            NSLog("user is \(user)")
            let repoQuery = repository.observeRepositoriesByOwner(login: user!.login)
            repoNotifier.updateQuery(newQuery: repoQuery)
        }
    }
    
    func onUserRepoUpdate(repos:[Repository]) {
        repos.forEach({ (repo) in
            NSLog("repo: \(repo.name)")
        })
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBOutlet weak var label: UILabel!
}
