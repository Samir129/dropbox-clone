import React, { useState, useEffect } from "react";
import axios from "axios";
import { ListGroup, Button } from "react-bootstrap";
import Loader from "./components/Loader";

function HomePage() {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [isDownloading, setIsDownloading] = useState(false);

  useEffect(() => {
    fetchFiles();
  }, []);

  const fetchFiles = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/files");
      setFiles(response.data);
    } catch (error) {
      console.error("Error fetching files:", error);
      alert("Error fetching files");
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    try {
      setIsUploading(true);
      const formData = new FormData();
      formData.append("file", selectedFile);

      await axios.post("http://localhost:8080/api/files", formData);
      await fetchFiles();
    } catch (error) {
      alert(error.response?.data?.message || "Upload failed");
    } finally {
      setIsUploading(false);
    }
  };

  const handleDownload = async (file) => {
    try {
      setIsDownloading(true);
      const response = await axios.get(
        `http://localhost:8080/api/files/${file.id}/download`,
        { responseType: "blob" }
      );

      // Create download link
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", file.fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      alert("Download failed: " + error.message);
    } finally {
      setIsDownloading(false);
    }
  };

  return (
    <div className="container mt-5">
      <h1>My Dropbox</h1>

      {/* Upload Section */}
      <div className="mb-4">
        <input
          type="file"
          onChange={(e) => setSelectedFile(e.target.files[0])}
          disabled={isUploading}
        />
        <Button
          variant="primary"
          onClick={handleUpload}
          disabled={!selectedFile || isUploading}
          className="ms-2"
        >
          {isUploading ? "Uploading..." : "Upload File"}
        </Button>
      </div>

      {/* Loader */}
      {(isUploading || isDownloading) && <Loader />}

      {/* File List */}
      <ListGroup>
        {files.map((file) => (
          <ListGroup.Item
            key={file.id}
            action
            onClick={() => handleDownload(file)}
            disabled={isDownloading}
          >
            {file.fileName}
          </ListGroup.Item>
        ))}
      </ListGroup>
    </div>
  );
}

export default HomePage;
