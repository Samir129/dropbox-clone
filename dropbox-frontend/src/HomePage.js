import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { ListGroup, Button } from 'react-bootstrap';

function HomePage(){
    const [files, setFiles] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);

    useEffect(() => {
        fetchFiles();
    }, [])

    const fetchFiles = async () => {
        const response = await axios.get('http://localhost:8000/api/files/');
        setFiles(response.data);
    }

    const handleUpload = async () => {
        const formData  = new FormData();
        formData.append('file', selectedFile);

        await axios.post('http://localhost:8000/api/files', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            }
        });
        fetchFiles();
    };

    return (
        <div className="container mt-5">
            <h1>My Dropbox</h1>
            <input type="file" onChange={(e) => setSelectedFile(e.target.files[0])}/>
            <Button variant="primary" onClick={handleUpload} className="mb-3">
                Upload File
            </Button>

            <ListGroup>
                {files.map(file => (
                    <ListGroup.Item key={file.id}>
                        <a
                            href={`http://localhost:8080/api/files/${file.id}/download`}
                            download
                        >
                            {file.fileName}
                        </a>
                    </ListGroup.Item>
                ))}
            </ListGroup>
        </div>
    );
}

export default HomePage;