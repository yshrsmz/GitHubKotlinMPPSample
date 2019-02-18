import UIKit
import data

class ViewController: UIViewController {
    
    let kodein = DIKt.doInitKodein()
    
    lazy var viewerKodein = ViewerModuleKt.getViewerKodein(dataKodein: self.kodein)
    
    lazy var repository = DIKt.getGitHubRepository(kodein: self.viewerKodein)
    
    lazy var mainViewModel:MainViewModel = ViewerModuleKt.getMainViewModel(viewerKodein: self.viewerKodein)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        
        mainViewModel.doInit()
        
//        repository.fetchViewer()
    }
    
    func onUserRepoUpdate(repos:[Repository]) {
        NSLog("repos: \(repos.count)")
//        repos.forEach({ (repo) in
//            NSLog("repo: \(repo.name)")
//        })
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBOutlet weak var label: UILabel!
}
