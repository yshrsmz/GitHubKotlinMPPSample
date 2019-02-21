import UIKit
import data

class ViewController: UIViewController {
    
    let kodein = DIKt.doInitKodein()
    
    lazy var viewerKodein = ViewerModuleKt.getViewerKodein(dataKodein: self.kodein)
    
//    lazy var mainViewModel:MainViewModel = ViewerModuleKt.getMainViewModel(viewerKodein: self.viewerKodein)
//
//    lazy var notifier:MainViewModelStateNotifier = ViewerModuleKt.getViewerViewModelStateNotifier(viewerKodein: self.viewerKodein)
    
    lazy var mainViewModel:MainViewModel2 = ViewerModuleKt.getMainViewModel2(viewerKodein: self.viewerKodein)
    
    lazy var notifier:MainViewModel2StateNotifier = ViewerModuleKt.getMainViewModel2StateNotifier(viewerKodein: self.viewerKodein)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        
        notifier.stateChanged(viewModel: mainViewModel) { (state) -> KotlinUnit in
            NSLog("stateChanged: \(state)")
            return KotlinUnit()
        }
        
        notifier.effectReceived(viewModel: mainViewModel) { (effect) -> KotlinUnit in
            NSLog("effectReceived: \(effect)")
            return KotlinUnit()

        }
        
        mainViewModel.dispatch(intent: MainIntent2.LoadViewer())
        
//        notifier.stateChanged(viewModel: mainViewModel) { (state) -> KotlinUnit in
//            NSLog("stateChanged: \(state)")
//            return KotlinUnit()
//        }
//
//        notifier.effectReceived(viewModel: mainViewModel) { (effect) -> KotlinUnit in
//            NSLog("effectReceived: \(effect)")
//            return KotlinUnit()
//        }
        
//        mainViewModel.doInit()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBOutlet weak var label: UILabel!
}
