import UIKit
import data

class ViewController: UIViewController {
    
    let kodein = DataModuleKt.doInitKodein()
    
    lazy var repository:GitHubRepositoryIos = { DataModuleKt.getGitHubRepository(kodein: self.kodein) }()
    
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
        printCurrentThread(label: "viewDidLoad")
        label.text = Proxy().proxyHello()
        
        let query = repository.observeViewer()
        userNotifier.updateQuery(newQuery: query)
        
        repository.fetchViewer()
    }
    
    func onUserUpdate(user: User?) {
        printCurrentThread(label: "onUserUpdate")
        if (user == nil) {
            NSLog("user is nil")
        } else {
            NSLog("user is \(user)")
            let repoQuery = repository.observeRepositoriesByOwner(login: user!.login)
            repoNotifier.updateQuery(newQuery: repoQuery)
        }
    }
    
    func onUserRepoUpdate(repos:[Repository]) {
        printCurrentThread(label: "onUserRepoUpdate")
        repos.forEach({ (repo) in
            NSLog("repo: \(repo.name)")
        })
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func printCurrentThread(label:String)  {
        NSLog("\(label) - current thread: \(Thread.current), \(OperationQueue.current?.underlyingQueue?.label ?? "None")")
    }
    
    
    @IBOutlet weak var label: UILabel!
}
