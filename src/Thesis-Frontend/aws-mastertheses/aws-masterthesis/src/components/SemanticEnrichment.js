import {useState} from "react";
import './componentsStyles/SemanticEnrichment.css'
import rake from 'rake-js'
import axios from "axios";
import InputTags from "react-input-tags-hooks";
import 'react-input-tags-hooks/build/index.css';
import HashLoader from "react-spinners/HashLoader";
import Modal from "react-bootstrap/Modal";
import {Markup} from 'interweave';

export default function SemanticEnrichment() {

    let fileReader;

    const [textFromFile, setTextFromFile] = useState('');
    const [file, setFile] = useState('');
    const [fileName, setFileName] = useState('');
    const [keywords, setKeywords] = useState('');
    const [keyAspects, setKeyAspects] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [enrichedAnnotations, setEnrichedAnnotations] = useState('');


    function handleFileRead(e) {
        const content = fileReader.result;
        setTextFromFile(content);
        setEnrichedAnnotations('');
    }


    function handleFileChosen(file) {
        fileReader = new FileReader();
        fileReader.onloadend = handleFileRead;
        fileReader.readAsText(file);
        setFile(file);
        setFileName(file.name);
    }

    function sendUserInputToBackend() {
        let formData = new FormData();
        formData.append("file", file);
        formData.append("keywords", keywords);
        formData.append("keyAspects", keyAspects);


        axios.post("http://localhost:8080/api/enrichment/userInputData", formData)
            .then(response => {
                console.log("User input data sent successfully");
                setEnrichedAnnotations(response.data);
                reset();
            }).catch(err => {
            console.log("User input data send failed: ", err);
            reset();
        })
    }


    function extractKeywordsWithRake() {
        const result = rake(textFromFile, {language: 'english',})
        var formatResult = result.join(', ')
        setKeywords(formatResult);
    }


    function handleKeyAspects(keyAspects) {
        setKeyAspects(keyAspects);
    }

    //After the data is submitted the values should reset to allow to continous upload of files
    function submitData() {
        if (keywords.length > 0 || keyAspects.length > 0) {
            setIsLoading(true);
            sendUserInputToBackend();
        }
    }

    function reset() {
        setFile('');
        setTextFromFile('');
        setKeywords('');
        setKeyAspects([]);
        document.getElementById('file').value = null;
        setIsLoading(false);
    }


    return (
        <div className="semanticEnrichment">
            <div>
                <h1>Welcome to the semantic metadata enrichment section of SMESE</h1>
                <p><span className="hintsAndTips">You have the options to upload a video content of your own choosing by
                    either <i>drag & drop</i> or
                    by clicking
                    on "<i>Choose File</i>" and selecting the file.</span></p>
            </div>
            <div className="inputFileDiv">
                <input
                    type='file'
                    id='file'
                    className='inputFile'
                    placeholder="Please upload your transcript"
                    accept='.txt'
                    onChange={e => handleFileChosen(e.target.files[0])}/>
            </div>

            {textFromFile.length > 0 ?
                <div className="extractedTextAndKeywords">
                    <p><span className="hintsAndTips"><i>If the following text is correct, please click on the button "<b>Extract
                        Keywords</b>" below
                        to
                        extract the main keywords within your uploaded file.</i></span></p>
                    <p><b>This is your uploaded text:</b></p>
                    <p className="extractedTextParagraph">{textFromFile}</p>
                    <div className="rakeExtraction">
                        <button type="button" onClick={extractKeywordsWithRake} className="btn btn-primary">
                            Extract Keywords
                        </button>
                    </div>
                </div>
                : null
            }

            {keywords.length > 0 ?
                <div className="extractedTextAndKeywords">
                    <p><span className="hintsAndTips">The following <b>keywords</b> were extracted which are divided by a
                        comma ", " </span></p>
                    <p className="extractedTextParagraph">{keywords}</p>
                </div>
                : null
            }

            {keywords.length > 0 ?
                <div className="keyAspects">
                    <p><span className="hintsAndTips"><i>In the following tags input field you have the option to type
                        relevant to the uploaded file
                        facts or concepts.</i></span></p>
                    <div className="keyAspectsInput">
                        <InputTags
                            onTag={handleKeyAspects}
                            tagColor='#48c774'
                            placeHolder="Type and press enter to add key aspects"
                        />
                    </div>
                </div>
                : null
            }

            {keywords.length > 0 ?
                <div className="submitData">
                    <button type="button" onClick={submitData} className="btn btn-primary">Save & Submit</button>
                </div>
                : null
            }

            {isLoading ?
                <div className="loaderModal">
                    <Modal show={isLoading} animation={false}>
                        <Modal.Header closeButton style={{
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                        }}>
                            <Modal.Title>Please wait ...</Modal.Title>
                            <div className="loaderAnimation">
                                <HashLoader color={'#007bff'} loading={isLoading} size={40}/>
                            </div>
                        </Modal.Header>

                    </Modal>
                </div>
                : null
            }


            {enrichedAnnotations.length > 0 ?
                <div className="enrichedAnnotations">
                    <p><b>The data for ''{fileName}'' was sucessfully acquired</b>.</p><br/>
                    {/*<p>{enrichedAnnotations}</p>*/}
                    <div><Markup content={enrichedAnnotations}/></div>
                </div>
                : null
            }

        </div>


    );
}


