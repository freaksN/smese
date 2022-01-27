import NavBar from "./components/NavBar";
import Home from "./components/Home";
import {HashRouter as Router, Route} from 'react-router-dom'
import SemanticEnrichment from "./components/SemanticEnrichment";
import SemanticSearch from "./components/SearchEngine";
import './App.css'

function App() {
    return (
        <Router>
            <div className="App">
                <NavBar/>
                <Route path="/" exact component={Home}/>
                <Route path="/enrich" exact component={SemanticEnrichment}/>
                <Route path="/search" exact component={SemanticSearch}/>
            </div>
        </Router>
    );
}

export default App;
