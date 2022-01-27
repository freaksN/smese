import {useState} from "react";
import "./componentsStyles/SearchEngine.css"
import axios from "axios";
import {Accordion, Button, Card, Form, FormControl, FormGroup} from "react-bootstrap";
import {InfoIconWithTooltip} from "icon-with-tooltip";

export default function SemanticSearch() {

    const [searchTermResult, setSearchTermResult] = useState({content: [], abstracts: [], keyaspects: [], name: []});
    const [expertSearch, setExpertSearch] = useState(false);


    function handleExpertSearchClicked(e) {
        setExpertSearch(e.target.checked)
    }

    function handleSearchSubmit(e) {
        e.preventDefault();

        var data = e.target[0].value;
        let formData = new FormData();
        formData.append("searchTerm", data);
        formData.append("expertSearch", expertSearch);
        axios.post('http://localhost:8080/api/searchEngine/searchTerm', formData)
            .then(response => {
                setSearchTermResult({content: response.data.content, abstracts: response.data.abstracts, keyaspects: response.data.keyaspects, name: response.data.name});
            }).catch(err => {
            console.log("search term send failed: ", err);
        })
    }

    return (
        <div className="searchEngine">
            <div className="welcomeText">
                <h1>Welcome to the search engine of SMESE</h1>
                <p><span className="hintsAndTips">You can search for video data by filling out the field below and pressing "<b>Enter</b>" or clicking
                    on the "<b>Search</b>" button.</span></p>
                <p><span className="hintsAndTips"><i>The "<b>Expert search</b>" option is recommended for advanced users.</i></span>
                </p>
            </div>
            <div className="searchField">
                <Form onSubmit={e => handleSearchSubmit(e)}>
                    <FormGroup>
                        <FormControl
                            type='text'
                            className='inputField'
                            placeholder='Search ...'>
                        </FormControl>
                        <div className="expertCheck">
                            <Form.Check type="checkbox" onChange={e => handleExpertSearchClicked(e)}
                                        label="Expert Search"
                                        style={{marginLeft: '1%'}}/>
                            <InfoIconWithTooltip text="If checked, SMESE will search for exact matches of: Linked Data URis, available key aspects, relevant conceptual families similar
                    to the ones presented in the home page." placement="right"/>
                        </div>
                        <Button className="submitButton" variant="primary" type="submit">Search</Button>
                    </FormGroup>
                </Form>
            </div>
            {(searchTermResult && searchTermResult.name.length > 0) ?
                <div className="searchResults">
                    <p>The following results were
                        found: <b>{searchTermResult.name.join(", ")}</b>
                    </p>
                    <div>
                        <p><span className="hintsAndTips">You can download your file using the button below.</span></p>
                        {searchTermResult && searchTermResult.content.map((content, idx) =>
                            <a  key={idx} href={window.URL.createObjectURL(new Blob([content]))}
                               download={searchTermResult.name[idx]}>
                                <Button style={{margin:'0 10px 10px 0'}} variant="primary">Download {(searchTermResult.name[idx])}</Button></a>
                        )}
                    </div>
                    {(searchTermResult && searchTermResult.abstracts.length > 0) ?
                        <div>
                            <p>Here are some brief overviews and summaries of relevant facts:</p>
                            <Accordion defaultActiveKey="1" style={{width: '80%'}}>
                                {searchTermResult && searchTermResult.abstracts.map((abstract, idx) =>
                                    <Card key={idx} style={{background: '#86e186'}}>
                                        <Accordion.Toggle as={Card.Header} eventKey="0">
                                            {Number(idx) + 1} - {abstract.substring(0, 15)}...
                                        </Accordion.Toggle>
                                        <Accordion.Collapse eventKey="0">
                                            <Card.Body style={{background: '#c7e3c7'}}>
                                                {abstract}
                                            </Card.Body>
                                        </Accordion.Collapse>
                                    </Card>
                                )}
                            </Accordion>
                        </div>
                        :
                        null
                    }
                    {searchTermResult && searchTermResult.keyaspects.length > 0 ?
                        <div>
                            <br/><p>Other relevant key aspects include: <br/>
                            <b>{searchTermResult.keyaspects.join(", ")}</b>
                        </p>
                        </div>
                        :
                        null
                    }
                </div>
                : null
            }
        </div>
    )
}