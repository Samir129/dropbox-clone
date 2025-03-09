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
        try {
            const response = await axios.get('http://localhost:8080/api/files');
            setFiles(response.data);
        } catch (error) {
            console.error('Error fetching files:', error);
            alert('Error fetching files');
        }
    }

    const handleUpload = async () => {
        if (!selectedFile) {
            alert('Please select a file to upload');
            return;
        }
        try{
            const formData  = new FormData();
            formData.append('file', selectedFile);

            await axios.post('http://localhost:8080/api/files', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                }
            });
            fetchFiles();
        }
        catch (error){
            console.error('Error uploading file:', error);
            alert('File upload failed');
        }
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