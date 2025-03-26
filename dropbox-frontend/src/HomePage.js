import React, { useState, useEffect } from "react";
import axios from "axios";
import { ListGroup, Button } from "react-bootstrap";
import Loader from "./components/Loader";
import { FaPlus } from "react-icons/fa";

const formatDate = (date) => {
  const options = {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  };
  return new Date(date).toLocaleDateString("en-US", options);
};

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
      <h1
        className="text-center text-primary mb-4"
        style={{
          fontFamily: "Arial, sans-serif",
          fontSize: "2.5rem",
          fontWeight: "700",
        }}
      >
        My Dropbox
      </h1>

      {/* Upload Section */}
      <div className="mb-4 d-flex align-items-center">
        <input
          type="file"
          id="file-upload"
          onChange={(e) => setSelectedFile(e.target.files[0])}
          disabled={isUploading}
          style={{ display: "none" }}
        />

        {/* Custom styled upload button */}
        <label
          htmlFor="file-upload" // Associate label with hidden input
          className="btn btn-primary rounded-circle p-0 d-flex align-items-center justify-content-center me-3"
          style={{
            cursor: "pointer",
            width: "40px",
            height: "40px",
            fontSize: "1rem",
          }}
          role="button"
          aria-label="Upload file"
        >
          <FaPlus /> {/* React Icons plus symbol */}
        </label>

        <Button
          variant="success"
          onClick={handleUpload}
          disabled={!selectedFile || isUploading}
          className="ms-auto"
          style={{ minWidth: "120px" }}
        >
          {isUploading ? (
            <span className="d-flex align-items-center">
              <span
                className="spinner-border spinner-border-sm me-2"
                role="status"
              />
              Uploading
            </span>
          ) : (
            "Upload File"
          )}
        </Button>

        {/* Show selected file name */}
        {selectedFile && (
          <span className="ms-3 text-muted">Selected: {selectedFile.name}</span>
        )}
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
            <div className="d-flex justify-content-between">
              <span>{file.fileName}</span>
              <span className="text-muted" style={{ fontSize: "0.875rem" }}>
                {file.lastModified ? formatDate(file.lastModified) : "No Date"}
              </span>
            </div>
          </ListGroup.Item>
        ))}
      </ListGroup>
    </div>
  );
}

export default HomePage;
