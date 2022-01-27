import './componentsStyles/Home.css'
import {Card} from "react-bootstrap";
import {useEffect, useState} from "react";
import axios from "axios";

export default function Home() {

    const [topFamilies, setTopFamilies] = useState([]);


    function fecthData() {
        axios.get('http://localhost:8080/api/')
            .then(response => {
                setTopFamilies(response.data);
            }).catch(err => {
            console.log("fetching top families failed: ", err);
        })
    }

    useEffect(() => {
        fecthData()
    }, []);

    return (
        <div className="home">
            <div className="jumbotron">
                <div className="container">
                    <h1 className="welcomeHeader">Welcome to SMESE</h1>
                    <h4 className="smeseInfo">SMESE is an Application for semantic metadata enrichment and semantic
                        search.
                        It is mainly developed to help <i>you</i> interact with digital semantic video archives and the
                        semantic web.</h4>
                    <p className="lead">Head over to the <a href="/#/enrich">Semantic Metadata Enrichment</a> section
                        to <i>upload</i> and automatically create semantically enriched metadata annotations.</p>
                    <p className="lead">Or go to <a href="/#/search">Semantic Search</a> section to search for a
                        information within the digital semantic video archive.</p>
                </div>
            </div>

            {(topFamilies && topFamilies.length > 0) ?
                <div>
                    <h1 className="hotTopicsHeader">TOP 5 MOST ACTIVE VIDEO THEMES</h1>
                    <div className="hotTopics">
                        {topFamilies.map((item, index) =>
                            <Card key={index} style={{width: '333px', margin: '0 30px 0 30px'}}
                                  className="hotThemeCards">
                                <Card.Body>
                                    <Card.Title>{item.split(',')[0]}</Card.Title>
                                    {/*<Card.Subtitle className="mb-2 text-muted">{item.split(',')[1]}</Card.Subtitle>*/}
                                    <Card.Text>
                                        {item.split(',')[1]}
                                    </Card.Text>
                                </Card.Body>
                            </Card>)}
                    </div>
                </div>
                : null
            }
        </div>
    )
}