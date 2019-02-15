import UIKit
import data

class ViewController: UIViewController {
    
    let kodein = DataModuleKt.doInitKodein()
    
    lazy var viewerKodein = ViewerModuleKt.getViewerKodein(dataKodein: self.kodein)
    
    lazy var repository = DataModuleKt.getGitHubRepository(kodein: self.viewerKodein)

//
    lazy var viewModel:ViewerViewModel = {ViewerModuleKt.getViewerViewModel(viewerKodein: self.viewerKodein)}()

    lazy var notifier:ViewerViewModelStateNotifier = ViewerModuleKt.getViewerViewModelStateNotifier(viewerKodein: self.viewerKodein)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        
        viewModel.doInit()
        
        notifier.stateChanged(viewModel: viewModel) { (state:ViewerState) -> KotlinUnit in
            NSLog("state: \(state)")
            if (state is ViewerState.Data) {
                let data = state as! ViewerState.Data
                NSLog("user: \(data.user)")
            }
            return KotlinUnit()
        }
        
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
