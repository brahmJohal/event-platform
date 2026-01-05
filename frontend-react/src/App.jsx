import { useState, useEffect, useRef } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  // System State
  const [totalEvents, setTotalEvents] = useState(0)
  const [activeStep, setActiveStep] = useState(0)
  const [logs, setLogs] = useState([])
  
  // Database Viewer State
  const [showDb, setShowDb] = useState(false)
  const [dbRecords, setDbRecords] = useState([])
  
  // Stress Test State
  const [autoMode, setAutoMode] = useState(false)
  const [latency, setLatency] = useState(0)
  const [systemHealth, setSystemHealth] = useState(100)

  const API_URL = "http://localhost:8080/api"

  // 1. Polling for Total Count
  useEffect(() => {
    fetchStats();
    const interval = setInterval(fetchStats, 2000);
    return () => clearInterval(interval);
  }, [])

  // 2. Auto-Pilot Logic
  useEffect(() => {
    let stressInterval;
    if (autoMode) {
      stressInterval = setInterval(() => {
        triggerEvent("AUTO_BOT");
        setSystemHealth(prev => Math.max(40, prev - Math.random() * 5)); 
      }, 800); 
    } else {
      setSystemHealth(100);
    }
    return () => clearInterval(stressInterval);
  }, [autoMode])

  const fetchStats = async () => {
    try {
      const res = await axios.get(`${API_URL}/analytics/count`)
      setTotalEvents(res.data.total_events)
    } catch (err) { /* ignore errors */ }
  }

  // --- NEW: FETCH DB RECORDS ---
  const openDatabase = async () => {
    setShowDb(true);
    try {
      // Fetch data from a wide time range to get recent events
      const res = await axios.get(`${API_URL}/analytics/time-range?from=2024-01-01T00:00:00&to=2030-01-01T00:00:00`)
      // Take only the last 50 records so browser doesn't crash
      setDbRecords(res.data.slice(-50).reverse()); 
    } catch (err) {
      alert("Failed to fetch Database info. Is backend running?");
    }
  }

  const addLog = (msg) => {
    const time = new Date().toLocaleTimeString('en-US', { hour12: false });
    setLogs(prev => [`[${time}] ${msg}`, ...prev].slice(0, 12));
  }

  const triggerEvent = async (user) => {
    const start = Date.now();
    setActiveStep(1); 

    try {
      await axios.post(`${API_URL}/events`, {
        type: "stress_test",
        payload: `User ${user} initiated heavy process cycle.`
      })
      
      const ping = Date.now() - start;
      setLatency(ping);

      setTimeout(() => {
        setActiveStep(2);
        if(!autoMode) addLog(`Packet received at Backend gateway.`);
        
        setTimeout(() => {
          setActiveStep(3);
          
          setTimeout(() => {
            setActiveStep(4);
            if(!autoMode) addLog(`Processed & Stored. Latency: ${ping}ms`);
            setTimeout(() => setActiveStep(0), 200);
          }, 150);
        }, 150);
      }, 150);

    } catch (err) {
      addLog("CRITICAL FAILURE: BACKEND OFFLINE");
      setAutoMode(false);
    }
  }

  return (
    <div className="dashboard-container">
      {/* HEADER */}
      <div className="header">
        <h1>EVENT_STREAM // PRO</h1>
        <div className="live-indicator">
          <span className="blink">●</span> SYSTEM ONLINE
        </div>
      </div>

      <div className="main-grid">
        
        {/* LEFT PANEL: CONTROLS */}
        <div className="panel">
          <h3>Operations Control</h3>
          <div style={{ marginBottom: '20px' }}>
            <label style={{ fontSize: '0.8rem', color: '#64748b' }}>TARGET USER ID</label>
            <input className="cyber-input" defaultValue="CMD_USER_01" disabled={autoMode} />
          </div>
          
          <button 
            className="cyber-btn" 
            onClick={() => triggerEvent("MANUAL_OP")}
            disabled={autoMode}
          >
            Transmit Single Signal
          </button>

          {/* --- NEW BUTTON HERE --- */}
          <button 
            className="cyber-btn" 
            style={{ marginTop: '20px', borderColor: '#facc15', color: '#facc15' }}
            onClick={openDatabase}
          >
            📂 Access Database Info
          </button>

          <div style={{ height: '30px' }}></div>

          <button 
            className={`cyber-btn ${autoMode ? 'active' : ''}`} 
            onClick={() => setAutoMode(!autoMode)}
          >
            {autoMode ? '⚠ ABORT AUTO-PILOT' : '⚠ ENGAGE AUTO-PILOT'}
          </button>
        </div>

        {/* MIDDLE PANEL: VISUALIZER */}
        <div className="panel">
          <h3>Real-Time Data Pipeline</h3>
          
          <div style={{ display: 'flex', gap: '40px', marginBottom: '40px' }}>
            <div>
              <div style={{ color: '#64748b', fontSize: '0.8rem' }}>TOTAL PROCESSED</div>
              <div className="big-number" style={{ color: '#00f3ff' }}>{totalEvents}</div>
            </div>
            <div>
              <div style={{ color: '#64748b', fontSize: '0.8rem' }}>LAST LATENCY</div>
              <div className="big-number" style={{ color: latency > 200 ? '#ff0055' : '#0aff00' }}>
                {latency}<span style={{fontSize: '1rem'}}>ms</span>
              </div>
            </div>
          </div>

          <div className="pipeline-container">
            <div className="pipe-line-bg"></div>
            <div className={`node ${activeStep >= 1 ? 'active' : ''}`}>💻<div className="node-label">React Client</div></div>
            <div className={`node ${activeStep >= 2 ? 'active' : ''}`}>⚙️<div className="node-label">Java API</div></div>
            <div className={`node ${activeStep >= 3 ? 'active' : ''}`}>🐍<div className="node-label">Python Worker</div></div>
            <div className={`node ${activeStep >= 4 ? 'active' : ''}`}>💾<div className="node-label">MongoDB</div></div>
          </div>
        </div>

        {/* RIGHT PANEL: SYSTEM HEALTH */}
        <div className="panel">
          <h3>System Metrics</h3>
          
          <div style={{ marginBottom: '20px' }}>
            <div className="stat-row"><span>SYSTEM HEALTH</span><span>{Math.round(systemHealth)}%</span></div>
            <div className="bar-container">
              <div className="bar-fill" style={{ width: `${systemHealth}%`, background: systemHealth < 50 ? '#ff0055' : '#0aff00' }}></div>
            </div>
          </div>

          <div className="terminal">
            {logs.map((log, i) => (
              <div key={i} className={`log-entry ${i === 0 ? 'new' : ''}`}>&gt; {log}</div>
            ))}
          </div>
        </div>
      </div>

      {/* --- DATABASE MODAL --- */}
      {showDb && (
        <div className="modal-overlay" onClick={() => setShowDb(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>MongoDB Records (Live View)</h2>
              <button className="close-btn" onClick={() => setShowDb(false)}>×</button>
            </div>
            <div className="db-viewer">
              {dbRecords.length === 0 ? "Querying Database..." : dbRecords.map((rec, i) => (
                <div key={i} className="json-record">
                  <div><span className="json-key">ID:</span> <span className="json-id">{rec.id}</span></div>
                  <div><span className="json-key">Type:</span> <span className="json-val">"{rec.type}"</span></div>
                  <div><span className="json-key">Payload:</span> <span className="json-val">"{rec.payload}"</span></div>
                  <div><span className="json-key">Score:</span> <span className="json-val">{rec.complexityScore}</span></div>
                  <div><span className="json-key">Timestamp:</span> <span className="json-val">{rec.processedAt}</span></div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default App