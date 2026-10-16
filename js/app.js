// CampusHire - Core Application Logic & State Controller

const App = {
  // Current active user state
  state: {
    role: 'STUDENT', // 'GUEST', 'STUDENT', 'TPO'
    user: {
      id: 1,
      fullName: 'Rahul Verma',
      email: 'rahul.verma@college.edu',
      rollNo: '21CS084',
      department: 'CSE',
      degree: 'B.Tech',
      cgpa: 8.70,
      skills: 'Java, SQL, Spring Boot, Problem Solving, React',
      verificationStatus: 'VERIFIED',
      profileScore: 85
    },
    drives: [],
    filteredDrives: [],
    applications: [],
    stats: {},
    announcements: [],
    interviews: [],
    activeFilter: 'all',
    searchQuery: '',
    selectedDrive: null,
    selectedApp: null
  },

  async init() {
    this.bindEvents();
    await this.loadInitialData();
    this.updateUserRoleUI();
    this.renderAll();
  },

  async loadInitialData() {
    try {
      // 1. Fetch drives
      const drivesData = await Api.getDrives();
      this.state.drives = Array.isArray(drivesData) ? drivesData : [];
      this.state.filteredDrives = [...this.state.drives];

      // 2. Fetch stats
      const statsData = await Api.getStats();
      this.state.stats = statsData || {};

      // 3. Fetch announcements
      const annData = await Api.getAnnouncements();
      this.state.announcements = Array.isArray(annData) ? annData : [];

      // 4. If student, fetch applications
      if (this.state.role === 'STUDENT' && this.state.user) {
        await this.refreshStudentApplications();
      }
    } catch (err) {
      console.error('Error loading initial data:', err);
      this.showToast('Connecting to local server...', 'info');
    }
  },

  async refreshStudentApplications() {
    try {
      const apps = await Api.getApplications(this.state.user.id);
      this.state.applications = Array.isArray(apps) ? apps : [];
    } catch (err) {
      console.error('Error fetching applications:', err);
    }
  },

  bindEvents() {
    // Search input
    const searchInput = document.getElementById('driveSearchInput');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        this.state.searchQuery = e.target.value.toLowerCase().trim();
        this.filterDrives();
      });
    }

    // Filter pills
    document.querySelectorAll('.filter-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
        e.currentTarget.classList.add('active');
        this.state.activeFilter = e.currentTarget.dataset.filter;
        this.filterDrives();
      });
    });

    // Close modals on overlay click or esc key
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
      overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
          this.closeAllModals();
        }
      });
    });

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') this.closeAllModals();
    });
  },

  filterDrives() {
    let list = [...this.state.drives];
    const q = this.state.searchQuery;

    if (q) {
      list = list.filter(d => 
        (d.companyName && d.companyName.toLowerCase().includes(q)) ||
        (d.roleTitle && d.roleTitle.toLowerCase().includes(q)) ||
        (d.requiredSkills && d.requiredSkills.toLowerCase().includes(q)) ||
        (d.location && d.location.toLowerCase().includes(q))
      );
    }

    if (this.state.activeFilter === 'eligible' && this.state.user) {
      const studentCgpa = this.state.user.cgpa || 0;
      list = list.filter(d => studentCgpa >= d.minCgpa);
    } else if (this.state.activeFilter === 'tier1') {
      list = list.filter(d => d.packageNum >= 8.0);
    } else if (this.state.activeFilter === 'tech') {
      list = list.filter(d => d.roleTitle.toLowerCase().includes('developer') || d.roleTitle.toLowerCase().includes('engineer'));
    }

    this.state.filteredDrives = list;
    this.renderDrivesGrid();
  },

  renderAll() {
    this.renderStatsStrip();
    this.renderDrivesGrid();
    this.renderHeroShowcase();
    this.renderStudentPortal();
    this.renderTpoOperationsDeck();
  },

  // 1. Stats Counter Strip
  renderStatsStrip() {
    const totalStudentsEl = document.getElementById('statTotalStudents');
    const visitingCompaniesEl = document.getElementById('statVisitingCompanies');
    const openDrivesEl = document.getElementById('statOpenDrives');
    const placementRateEl = document.getElementById('statPlacementSuccess');

    if (totalStudentsEl) totalStudentsEl.textContent = '500+';
    if (visitingCompaniesEl) visitingCompaniesEl.textContent = '50+';
    if (openDrivesEl) openDrivesEl.textContent = `${this.state.drives.length * 15}+`;
    if (placementRateEl) placementRateEl.textContent = '95%';
  },

  // 2. Drives Grid
  renderDrivesGrid() {
    const container = document.getElementById('drivesGrid');
    if (!container) return;

    if (this.state.filteredDrives.length === 0) {
      container.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 48px 20px; background: rgba(255,255,255,0.02); border-radius: 14px; border: 1px dashed var(--border-subtle);">
          <span style="font-size: 2.5rem; display: block; margin-bottom: 12px;">🔍</span>
          <h4 style="font-size: 1.2rem; color: var(--text-heading); margin-bottom: 6px;">No Placement Drives Found</h4>
          <p style="color: var(--text-muted); font-size: 0.9rem;">Try adjusting your keyword filter or CGPA eligibility threshold.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = this.state.filteredDrives.map(drive => {
      const isEligible = this.state.user ? (this.state.user.cgpa >= drive.minCgpa) : true;
      const skillsArr = drive.requiredSkills ? drive.requiredSkills.split(',').map(s => s.trim()) : [];
      const hasApplied = this.state.applications.some(a => a.driveId === drive.id);

      return `
        <div class="drive-card">
          <div>
            <div class="drive-card-header">
              <div class="drive-company-badge">
                <div class="company-logo-circle" style="background: ${drive.companyColorAccent || '#4F46E5'}">
                  ${drive.companyLogoAbbr || 'CO'}
                </div>
                <div class="company-meta">
                  <h4>${this.escapeHtml(drive.companyName)}</h4>
                  <span>${this.escapeHtml(drive.companyCategory || 'Enterprise Partner')}</span>
                </div>
              </div>
              <span class="verified-pill">✔ Verified</span>
            </div>

            <h3 class="drive-role-title">${this.escapeHtml(drive.roleTitle)}</h3>
            <div class="drive-location">
              <span>📍</span> ${this.escapeHtml(drive.location || 'India')}
            </div>

            <div class="drive-info-row">
              <span class="drive-info-label">Package (CTC)</span>
              <span class="drive-package-value">${this.escapeHtml(drive.packageCtc)}</span>
            </div>

            <div class="drive-skills-wrap">
              <div class="drive-skills-title">Required Skills</div>
              <div class="skill-tags">
                ${skillsArr.map(sk => `<span class="skill-tag">${this.escapeHtml(sk)}</span>`).join('')}
              </div>
            </div>

            <div class="drive-criteria-row">
              <span class="min-cgpa-pill">Min CGPA: <strong>${drive.minCgpa.toFixed(1)}+</strong></span>
              <span class="deadline-pill">⏰ ${this.escapeHtml(drive.deadlineDays || 'Active')}</span>
            </div>
          </div>

          <div class="drive-actions-row">
            <button class="btn btn-secondary btn-sm" onclick="App.openDriveModal(${drive.id})">
              View Details
            </button>
            ${hasApplied ? `
              <button class="btn btn-secondary btn-sm" disabled style="opacity: 0.7; color: var(--emerald-500); border-color: rgba(16,185,129,0.3)">
                ✔ Applied
              </button>
            ` : isEligible ? `
              <button class="btn btn-primary btn-sm" onclick="App.applyDirectly(${drive.id})">
                1-Click Apply
              </button>
            ` : `
              <button class="btn btn-secondary btn-sm" disabled title="Min CGPA ${drive.minCgpa} required" style="opacity: 0.5; cursor: not-allowed;">
                Ineligible (CGPA)
              </button>
            `}
          </div>
        </div>
      `;
    }).join('');
  },

  // 3. Hero Showcase Card
  renderHeroShowcase() {
    const greetingEl = document.getElementById('heroStudentGreeting');
    const branchEl = document.getElementById('heroStudentBranch');
    const drivesListEl = document.getElementById('heroMiniDrivesList');

    if (greetingEl && this.state.user) {
      greetingEl.innerHTML = `Good Morning, ${this.escapeHtml(this.state.user.fullName.split(' ')[0])} 👋`;
    }
    if (branchEl && this.state.user) {
      branchEl.textContent = `${this.state.user.degree} ${this.state.user.department} (${this.state.user.batchYear || 2025} Batch) • CGPA ${this.state.user.cgpa.toFixed(1)}`;
    }

    if (drivesListEl) {
      const top3 = this.state.drives.slice(0, 3);
      drivesListEl.innerHTML = top3.map(d => {
        const hasApplied = this.state.applications.some(a => a.driveId === d.id);
        return `
          <div class="mini-opp-card">
            <div class="mini-opp-left">
              <div class="company-logo-avatar" style="background: ${d.companyColorAccent || '#3B82F6'}">
                ${d.companyLogoAbbr || 'C'}
              </div>
              <div class="mini-opp-details">
                <h4>${this.escapeHtml(d.roleTitle)} <span class="mini-eligible-badge">Eligible</span></h4>
                <p>${this.escapeHtml(d.companyName)} • ${this.escapeHtml(d.packageCtc)} • ${this.escapeHtml(d.location ? d.location.split(',')[0] : 'Remote')}</p>
              </div>
            </div>
            ${hasApplied ? `
              <button class="btn btn-secondary btn-sm" style="color: var(--emerald-500); font-size: 0.72rem; padding: 4px 8px;" disabled>
                Applied
              </button>
            ` : `
              <button class="btn btn-primary btn-sm" style="font-size: 0.72rem; padding: 4px 10px;" onclick="App.applyDirectly(${d.id})">
                Apply
              </button>
            `}
          </div>
        `;
      }).join('');
    }
  },

  // 4. Student Portal
  renderStudentPortal() {
    const welcomeEl = document.getElementById('portalWelcomeName');
    const appsListEl = document.getElementById('portalAppsList');
    const appliedCountEl = document.getElementById('portalAppliedCount');
    const offersCountEl = document.getElementById('portalOffersCount');

    if (welcomeEl && this.state.user) {
      welcomeEl.textContent = `Welcome back, ${this.state.user.fullName}`;
    }

    if (appliedCountEl) {
      appliedCountEl.textContent = this.state.applications.length;
    }

    const offers = this.state.applications.filter(a => a.offerPackage || a.currentStage.includes('OFFER'));
    if (offersCountEl) {
      offersCountEl.textContent = offers.length;
    }

    if (appsListEl) {
      if (this.state.applications.length === 0) {
        appsListEl.innerHTML = `
          <div style="text-align: center; padding: 24px; color: var(--text-muted); font-size: 0.88rem;">
            No applications submitted yet. Browse open drives above to apply with 1-click!
          </div>
        `;
        return;
      }

      appsListEl.innerHTML = this.state.applications.map(app => {
        let badgeStyle = 'background: rgba(99,102,241,0.15); color: #818CF8;';
        if (app.currentStage === 'SHORTLISTED') {
          badgeStyle = 'background: rgba(16,185,129,0.15); color: var(--emerald-500);';
        } else if (app.currentStage.includes('INTERVIEW')) {
          badgeStyle = 'background: rgba(245,158,11,0.15); color: var(--amber-500);';
        } else if (app.currentStage.includes('OFFER')) {
          badgeStyle = 'background: rgba(16,185,129,0.25); color: #34D399; font-weight: 800;';
        }

        return `
          <div class="app-row-card">
            <div class="app-row-info">
              <h5>${this.escapeHtml(app.companyName)}</h5>
              <p>${this.escapeHtml(app.roleTitle)} • Rounds: ${this.escapeHtml(app.roundsCompleted || '1/3')}</p>
            </div>
            <div class="app-row-actions">
              <span class="pill-badge" style="${badgeStyle}">
                ${this.escapeHtml(app.stageLabel || app.currentStage)}
              </span>
              <button class="btn btn-secondary btn-sm" onclick="App.openRoundsModal(${app.id})">
                ${app.currentStage.includes('INTERVIEW') ? 'Schedule' : 'View Rounds'}
              </button>
            </div>
          </div>
        `;
      }).join('');
    }
  },

  // 5. TPO Operations Deck
  renderTpoOperationsDeck() {
    const tpoStudentsCount = document.getElementById('tpoDeckStudents');
    const tpoCompaniesCount = document.getElementById('tpoDeckCompanies');
    const tpoDrivesCount = document.getElementById('tpoDeckDrives');
    const tpoAppsCount = document.getElementById('tpoDeckApplications');
    const tpoOffersCount = document.getElementById('tpoDeckOffers');

    if (tpoStudentsCount) tpoStudentsCount.textContent = '1,240';
    if (tpoCompaniesCount) tpoCompaniesCount.textContent = '58';
    if (tpoDrivesCount) tpoDrivesCount.textContent = '112';
    if (tpoAppsCount) tpoAppsCount.textContent = '3,420';
    if (tpoOffersCount) tpoOffersCount.textContent = '482 Offers Issued';
  },

  // 1-Click Apply Action
  async applyDirectly(driveId) {
    if (!this.state.user) {
      this.openModal('studentLoginModal');
      this.showToast('Please login as a student to apply', 'info');
      return;
    }

    try {
      const res = await Api.applyToDrive(this.state.user.id, driveId);
      if (res.success) {
        this.showToast(res.message || 'Application submitted successfully!', 'success');
        await this.refreshStudentApplications();
        this.renderAll();
      } else {
        this.showToast(res.error || 'Failed to apply', 'error');
      }
    } catch (err) {
      this.showToast('Error communicating with server', 'error');
    }
  },

  // Open Drive Details Modal
  openDriveModal(driveId) {
    const drive = this.state.drives.find(d => d.id === driveId);
    if (!drive) return;
    this.state.selectedDrive = drive;

    const modalTitle = document.getElementById('driveModalTitle');
    const modalContent = document.getElementById('driveModalContent');
    const applyBtn = document.getElementById('driveModalApplyBtn');

    if (modalTitle) modalTitle.textContent = `${drive.companyName} — ${drive.roleTitle}`;

    const isEligible = this.state.user ? (this.state.user.cgpa >= drive.minCgpa) : true;
    const hasApplied = this.state.applications.some(a => a.driveId === drive.id);

    if (modalContent) {
      modalContent.innerHTML = `
        <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 20px;">
          <div class="company-logo-circle" style="background: ${drive.companyColorAccent || '#4F46E5'}; width: 56px; height: 56px; font-size: 1.4rem;">
            ${drive.companyLogoAbbr || 'CO'}
          </div>
          <div>
            <h3 style="font-size: 1.3rem; color: var(--text-heading);">${this.escapeHtml(drive.companyName)}</h3>
            <p style="color: var(--text-muted); font-size: 0.88rem;">${this.escapeHtml(drive.companyCategory)} • ${this.escapeHtml(drive.location || 'India')}</p>
          </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px; background: rgba(255,255,255,0.03); padding: 16px; border-radius: 12px; border: 1px solid var(--border-subtle);">
          <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">Annual CTC Package</span>
            <div style="font-size: 1.25rem; font-weight: 800; color: #60A5FA;">${this.escapeHtml(drive.packageCtc)}</div>
          </div>
          <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">Minimum Cut-off</span>
            <div style="font-size: 1.25rem; font-weight: 800; color: ${isEligible ? 'var(--emerald-500)' : 'var(--rose-500)'}">
              ${drive.minCgpa.toFixed(1)}+ CGPA ${isEligible ? '✔ Eligible' : '✘ Below Cut-off'}
            </div>
          </div>
          <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">Eligible Branches</span>
            <div style="font-size: 0.95rem; font-weight: 700; color: var(--text-heading);">${this.escapeHtml(drive.eligibleBranches || 'All Engineering')}</div>
          </div>
          <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">Application Window</span>
            <div style="font-size: 0.95rem; font-weight: 700; color: var(--amber-500);">${this.escapeHtml(drive.deadlineDays || 'Active Drive')}</div>
          </div>
        </div>

        <h4 style="font-size: 1rem; color: var(--text-heading); margin-bottom: 8px;">Key Technical Requirements:</h4>
        <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 20px; line-height: 1.6;">
          Candidates must demonstrate proficiency in <strong>${this.escapeHtml(drive.requiredSkills)}</strong>, sound fundamental knowledge of Data Structures, OOPs paradigms, and institutional academic standing.
        </p>

        <h4 style="font-size: 1rem; color: var(--text-heading); margin-bottom: 12px;">Selection Stages:</h4>
        <div style="display: flex; flex-direction: column; gap: 8px; font-size: 0.85rem; color: var(--text-muted);">
          <div>1️⃣ <strong>Round 1:</strong> Online Computer Adaptive Assessment (DSA & Cognitive)</div>
          <div>2️⃣ <strong>Round 2:</strong> In-depth Technical Architecture & Live Coding</div>
          <div>3️⃣ <strong>Round 3:</strong> Executive HR Interview & Placement Offer Formalization</div>
        </div>
      `;
    }

    if (applyBtn) {
      if (hasApplied) {
        applyBtn.textContent = 'Already Applied';
        applyBtn.disabled = true;
      } else if (!isEligible) {
        applyBtn.textContent = 'Ineligible (CGPA Below Cut-off)';
        applyBtn.disabled = true;
      } else {
        applyBtn.textContent = 'Submit 1-Click Application';
        applyBtn.disabled = false;
        applyBtn.onclick = () => {
          this.closeAllModals();
          this.applyDirectly(drive.id);
        };
      }
    }

    this.openModal('driveDetailsModal');
  },

  // Open Rounds Progression Modal (Matches PDF Page 6 "View Rounds")
  openRoundsModal(applicationId) {
    const app = this.state.applications.find(a => a.id === applicationId);
    if (!app) return;
    this.state.selectedApp = app;

    const modalTitle = document.getElementById('roundsModalTitle');
    const modalContent = document.getElementById('roundsModalContent');

    if (modalTitle) {
      modalTitle.textContent = `${app.companyName} — Application Round Progression`;
    }

    const isZohoOffer = app.companyName.includes('Zoho') && (app.offerPackage || app.currentStage === 'SHORTLISTED');

    if (modalContent) {
      modalContent.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid var(--border-subtle);">
          <div>
            <h4 style="font-size: 1.15rem; color: var(--text-heading);">${this.escapeHtml(app.roleTitle)}</h4>
            <p style="color: var(--text-muted); font-size: 0.85rem;">Candidate: ${this.escapeHtml(app.studentName || this.state.user.fullName)} (${this.escapeHtml(app.studentRollNo || this.state.user.rollNo)})</p>
          </div>
          <span class="pill-badge" style="background: rgba(16,185,129,0.15); color: var(--emerald-500); font-weight: 700;">
            ${this.escapeHtml(app.stageLabel || app.currentStage)}
          </span>
        </div>

        <div style="display: flex; flex-direction: column; gap: 18px; margin-bottom: 28px;">
          <!-- Round 1 -->
          <div style="display: flex; gap: 14px;">
            <div style="display: flex; flex-direction: column; align-items: center;">
              <div style="width: 28px; height: 28px; border-radius: 50%; background: var(--emerald-500); color: white; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 800;">✔</div>
              <div style="width: 2px; height: 35px; background: var(--emerald-500);"></div>
            </div>
            <div>
              <h5 style="font-size: 0.95rem; color: var(--text-heading);">Round 1: Online Technical Aptitude Assessment</h5>
              <p style="font-size: 0.8rem; color: var(--emerald-500); font-weight: 600;">Cleared (Score: 92/100) • Institutional Scorecard Sync</p>
            </div>
          </div>

          <!-- Round 2 -->
          <div style="display: flex; gap: 14px;">
            <div style="display: flex; flex-direction: column; align-items: center;">
              <div style="width: 28px; height: 28px; border-radius: 50%; background: ${app.currentStage === 'APPLIED' ? 'var(--text-subtle)' : 'var(--emerald-500)'}; color: white; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 800;">
                ${app.currentStage === 'APPLIED' ? '2' : '✔'}
              </div>
              <div style="width: 2px; height: 35px; background: ${app.currentStage === 'APPLIED' ? 'var(--border-subtle)' : 'var(--emerald-500)'};"></div>
            </div>
            <div>
              <h5 style="font-size: 0.95rem; color: var(--text-heading);">Round 2: Technical Interview & Live Problem Solving</h5>
              <p style="font-size: 0.8rem; color: var(--text-muted);">${this.escapeHtml(app.nextStepDetails || 'Scheduled in campus virtual suites')}</p>
            </div>
          </div>

          <!-- Round 3 -->
          <div style="display: flex; gap: 14px;">
            <div style="display: flex; flex-direction: column; align-items: center;">
              <div style="width: 28px; height: 28px; border-radius: 50%; background: ${app.isOfferAccepted ? 'var(--emerald-500)' : '#4F46E5'}; color: white; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 800;">
                ${app.isOfferAccepted ? '✔' : '3'}
              </div>
            </div>
            <div>
              <h5 style="font-size: 0.95rem; color: var(--text-heading);">Round 3: Leadership Discussion & Offer Release</h5>
              <p style="font-size: 0.8rem; color: var(--text-muted);">Package Range: ${this.escapeHtml(app.offerPackage || app.packageCtc || '₹10.5 LPA')}</p>
            </div>
          </div>
        </div>

        ${(app.offerPackage || isZohoOffer) ? `
          <div style="background: linear-gradient(135deg, rgba(16,185,129,0.15), rgba(79,70,229,0.15)); border: 1px solid rgba(16,185,129,0.4); border-radius: 12px; padding: 20px; text-align: center;">
            <span style="font-size: 2rem; display: block; margin-bottom: 8px;">🎉</span>
            <h4 style="font-size: 1.15rem; color: var(--text-heading); margin-bottom: 4px;">Placement Offer Extended!</h4>
            <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 16px;">
              Congratulations! ${this.escapeHtml(app.companyName)} has extended a full-time corporate offer of <strong>${this.escapeHtml(app.offerPackage || '₹10.5 LPA')}</strong>.
            </p>
            ${app.isOfferAccepted ? `
              <div style="padding: 10px; background: var(--emerald-bg); color: var(--emerald-500); font-weight: 700; border-radius: 8px;">
                ✔ Offer Formally Accepted & Institutional Records Updated
              </div>
            ` : `
              <button class="btn btn-emerald btn-lg" onclick="App.acceptOfferAction(${app.id})">
                Accept Official Placement Offer
              </button>
            `}
          </div>
        ` : ''}
      `;
    }

    this.openModal('roundsModal');
  },

  async acceptOfferAction(applicationId) {
    try {
      const res = await Api.acceptOffer(applicationId);
      if (res.success) {
        this.showToast(res.message || 'Offer accepted!', 'success');
        this.closeAllModals();
        await this.refreshStudentApplications();
        this.renderAll();
      } else {
        this.showToast(res.error || 'Failed to accept offer', 'error');
      }
    } catch (err) {
      this.showToast('Error accepting offer', 'error');
    }
  },

  // Switch Role
  switchRole(role) {
    this.state.role = role;
    if (role === 'STUDENT') {
      this.state.user = {
        id: 1,
        fullName: 'Rahul Verma',
        email: 'rahul.verma@college.edu',
        rollNo: '21CS084',
        department: 'CSE',
        degree: 'B.Tech',
        cgpa: 8.70,
        skills: 'Java, SQL, Spring Boot, Problem Solving, React',
        verificationStatus: 'VERIFIED',
        profileScore: 85
      };
      this.showToast('Switched to Student mode (Rahul Verma)', 'info');
      this.refreshStudentApplications().then(() => this.renderAll());
    } else if (role === 'TPO') {
      this.state.user = {
        id: 1,
        name: 'Dr. K. Ramanathan',
        title: 'Head of Training & Placement (TPO)',
        email: 'tpo@college.edu'
      };
      this.showToast('Switched to TPO Operations Admin mode', 'info');
    } else {
      this.state.user = null;
      this.state.applications = [];
      this.showToast('Switched to Guest mode', 'info');
    }

    this.updateUserRoleUI();
    this.renderAll();
  },

  updateUserRoleUI() {
    const rolePill = document.getElementById('activeRolePill');
    const roleDot = document.getElementById('activeRoleDot');
    const roleName = document.getElementById('activeRoleName');

    if (roleDot) {
      roleDot.className = 'role-dot';
      if (this.state.role === 'TPO') roleDot.classList.add('tpo');
      else if (this.state.role === 'GUEST') roleDot.classList.add('guest');
    }

    if (roleName) {
      if (this.state.role === 'STUDENT') roleName.textContent = 'Rahul Verma (Student)';
      else if (this.state.role === 'TPO') roleName.textContent = 'Dr. Ramanathan (TPO)';
      else roleName.textContent = 'Guest Visitor';
    }
  },

  // Modal Open/Close
  openModal(modalId) {
    const m = document.getElementById(modalId);
    if (m) m.classList.add('open');
  },

  closeAllModals() {
    document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('open'));
  },

  // Schedule Drive Action (TPO)
  async handleScheduleDriveSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const driveData = {
      companyId: parseInt(form.companyId.value),
      roleTitle: form.roleTitle.value.trim(),
      location: form.location.value.trim(),
      packageCtc: form.packageCtc.value.trim(),
      packageNum: parseFloat(form.packageNum.value) || 8.0,
      minCgpa: parseFloat(form.minCgpa.value) || 7.0,
      eligibleBranches: form.eligibleBranches.value.trim(),
      requiredSkills: form.requiredSkills.value.trim(),
      deadlineDays: form.deadlineDays.value.trim(),
      openings: parseInt(form.openings.value) || 30
    };

    try {
      const res = await Api.scheduleDrive(driveData);
      if (res.success) {
        this.showToast('New campus placement drive scheduled successfully!', 'success');
        this.closeAllModals();
        form.reset();
        await this.loadInitialData();
        this.renderAll();
      } else {
        this.showToast(res.error || 'Failed to schedule drive', 'error');
      }
    } catch (err) {
      this.showToast('Error communicating with Java backend', 'error');
    }
  },

  // Export Eligibility List CSV
  downloadEligibilityCsv() {
    const driveId = this.state.drives.length > 0 ? this.state.drives[0].id : 1;
    const url = Api.getExportUrl(driveId);
    window.open(url, '_blank');
    this.showToast('Generating official NAAC/NIRF eligibility export...', 'success');
  },

  // Toast Notification System
  showToast(message, type = 'info') {
    let container = document.getElementById('toastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toastContainer';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icon = type === 'success' ? '✔' : type === 'error' ? '⚠️' : 'ℹ️';

    toast.innerHTML = `
      <span class="toast-icon">${icon}</span>
      <span class="toast-message">${this.escapeHtml(message)}</span>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4000);
  },

  escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }
};

window.App = App;

// Bootstrap on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  App.init();
});
