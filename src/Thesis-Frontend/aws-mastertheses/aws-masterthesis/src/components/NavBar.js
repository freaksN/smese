export default function NavBar() {
    return (
        <div className="NavBar">

            <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav mr-auto">
                        <li className="nav-item">
                            <a className="nav-link" href="/">Home <span className="sr-only">(current)</span></a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#/enrich">Semantic Metadata Enrichment</a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#/search">Semantic Search</a>
                        </li>
                    </ul>
                </div>
            </nav>

        </div>
    );
}


