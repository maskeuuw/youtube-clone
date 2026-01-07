<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<style type="text/css">
	/*
        2. Custom CSS Grid Implementation (YouTube-like layout)
        This overrides Bootstrap's row/col for a fixed, clean grid.
        */
        
        body {
            background-color: #f9f9f9; /* Light background */
            font-family: Roboto, Arial, sans-serif;
        }

        .video-feed-container {
            /* Centers the content and limits the max width like YouTube */
            max-width: 1500px; 
            margin: 0 auto;
            padding: 20px 15px;
        }

        /* 🖼️ CSS GRID LAYOUT for Rows and Columns */
        .video-grid {
            display: grid;
            /* Creates columns that are at least 300px wide for large screens.
               The 'auto-fit' makes it responsive by fitting as many columns as possible. */
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 40px 16px; /* Vertical gap (rows) 40px, Horizontal gap (columns) 16px */
        }

        /* Video Card Styling */
        .video-card {
            text-decoration: none; /* Remove underline from links */
            color: inherit;
        }

        /* Thumbnail Aspect Ratio Container (16:9) */
        .thumbnail-container {
            position: relative;
            width: 100%;
            padding-top: 56.25%; /* (9 / 16 * 100) */
            overflow: hidden;
            border-radius: 12px;
            margin-bottom: 12px;
        }

        .thumbnail-container img {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        /* Video Details (Avatar and Text) */
        .video-details {
            display: flex;
        }

        .channel-avatar {
            width: 36px;
            height: 36px;
            min-width: 36px; /* Prevents shrinking */
            border-radius: 50%;
            background-color: #ccc; /* Placeholder color */
            margin-right: 10px;
        }

        .metadata {
            flex-grow: 1;
        }

        .video-title {
            font-size: 1.1rem;
            font-weight: 600;
            line-height: 1.4;
            /* Limits title to two lines */
            max-height: 2.8em; 
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin: 0 0 4px 0;
        }

        .channel-name, .video-stats {
            font-size: 0.9rem;
            color: #606060;
            margin: 0;
        }
    
	
</style>
</head>
<body>
	<div class="video-feed-container">
        <div class="video-grid">
            
            <a href="video?id=123" class="video-card">
                <div class="thumbnail-container">
                    <img src="https://via.placeholder.com/640x360?text=Video+Thumbnail+1" alt="Video Title Example">
                </div>
                <div class="video-details">
                    <div class="channel-avatar">
                        </div>
                    <div class="metadata">
                        <h3 class="video-title">This is a long example video title that might span two lines before being truncated.</h3>
                        <p class="channel-name">Awesome Channel Name</p>
                        <p class="video-stats">1.2M views • 3 days ago</p>
                    </div>
                </div>
            </a>
            
            <a href="video?id=456" class="video-card">
                <div class="thumbnail-container">
                    <img src="https://via.placeholder.com/640x360?text=Video+Thumbnail+2" alt="Short Title">
                </div>
                <div class="video-details">
                    <div class="channel-avatar"></div>
                    <div class="metadata">
                        <h3 class="video-title">Short Title</h3>
                        <p class="channel-name">Tech Reviewer</p>
                        <p class="video-stats">50K views • 1 year ago</p>
                    </div>
                </div>
            </a>
            
            <a href="video?id=789" class="video-card">
                <div class="thumbnail-container">
                    <img src="https://via.placeholder.com/640x360?text=Video+Thumbnail+3" alt="Travel Vlog">
                </div>
                <div class="video-details">
                    <div class="channel-avatar"></div>
                    <div class="metadata">
                        <h3 class="video-title">My Amazing Travel Vlog in the Mountains (Part 1)</h3>
                        <p class="channel-name">Global Explorer</p>
                        <p class="video-stats">200K views • 2 weeks ago</p>
                    </div>
                </div>
            </a>
            
            </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>