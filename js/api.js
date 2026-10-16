// CampusHire - Frontend API Client (Communicates with Java Backend on /api/*)

const API_BASE = window.location.origin.includes('localhost') 
  ? `${window.location.origin}/api` 
  : '/api';

const Api = {
  // 1. System Health & Platform Stats
  async getHealth() {
    const res = await fetch(`${API_BASE}/health`);
    return await res.json();
  },

  async getStats() {
    const res = await fetch(`${API_BASE}/stats`);
    return await res.json();
  },

  // 2. Drives Management
  async getDrives(activeOnly = false) {
    const url = activeOnly ? `${API_BASE}/drives?active=true` : `${API_BASE}/drives`;
    const res = await fetch(url);
    return await res.json();
  },

  async scheduleDrive(driveData) {
    const res = await fetch(`${API_BASE}/drives`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(driveData)
    });
    return await res.json();
  },

  async updateDriveStatus(driveId, status) {
    const res = await fetch(`${API_BASE}/drives/${driveId}/status`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status })
    });
    return await res.json();
  },

  // 3. Authentication
  async loginStudent(email, password) {
    const res = await fetch(`${API_BASE}/auth/student/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    return await res.json();
  },

  async loginTpo(email, password) {
    const res = await fetch(`${API_BASE}/auth/tpo/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    return await res.json();
  },

  async registerStudent(studentData) {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(studentData)
    });
    return await res.json();
  },

  // 4. Students
  async getStudents() {
    const res = await fetch(`${API_BASE}/students`);
    return await res.json();
  },

  async getStudent(id) {
    const res = await fetch(`${API_BASE}/students/${id}`);
    return await res.json();
  },

  async updateStudent(id, data) {
    const res = await fetch(`${API_BASE}/students/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return await res.json();
  },

  async verifyStudent(id, status = 'VERIFIED') {
    const res = await fetch(`${API_BASE}/students/${id}/verify`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status })
    });
    return await res.json();
  },

  // 5. Applications & Offer Tracking
  async getApplications(studentId = null) {
    const url = studentId ? `${API_BASE}/applications?studentId=${studentId}` : `${API_BASE}/applications`;
    const res = await fetch(url);
    return await res.json();
  },

  async applyToDrive(studentId, driveId) {
    const res = await fetch(`${API_BASE}/applications`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ studentId, driveId })
    });
    return await res.json();
  },

  async updateStage(appId, stageData) {
    const res = await fetch(`${API_BASE}/applications/${appId}/stage`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(stageData)
    });
    return await res.json();
  },

  async acceptOffer(appId) {
    const res = await fetch(`${API_BASE}/applications/${appId}/accept-offer`, {
      method: 'POST'
    });
    return await res.json();
  },

  // 6. Announcements, Interviews & CSV Export
  async getAnnouncements() {
    const res = await fetch(`${API_BASE}/announcements`);
    return await res.json();
  },

  async createAnnouncement(data) {
    const res = await fetch(`${API_BASE}/announcements`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return await res.json();
  },

  async getInterviews() {
    const res = await fetch(`${API_BASE}/interviews`);
    return await res.json();
  },

  getExportUrl(driveId) {
    return `${API_BASE}/export/eligibility?driveId=${driveId}`;
  }
};

window.Api = Api;
