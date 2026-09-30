import re

with open('c:/ALTGRAPH/docs/index.html', 'r', encoding='utf-8') as f:
    content = f.read()

new_css = """
        :root {
            --bg-dark: #000000;
            --bg-surface: #111111;
            --card-bg: rgba(25, 25, 25, 0.7);
            --card-border: #333333;
            --accent-blue: #007aff;
            --accent-blue-hover: #0056b3;
            --accent-green: #34c759;
            --accent-yellow: #ffcc00;
            --accent-red: #ff3b30;
            --accent-purple: #af52de;
            --text-main: #ffffff;
            --text-muted: #888888;
            --text-kicker: #aaaaaa;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            -webkit-font-smoothing: antialiased;
        }

        body {
            background-color: var(--bg-dark);
            color: var(--text-main);
            line-height: 1.6;
            padding-bottom: 80px;
            overflow-x: hidden;
        }

        header {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo-container {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .logo-container img {
            width: 48px;
            height: 48px;
            border-radius: 12px;
        }

        .logo-container h1 {
            font-size: 1.7rem;
            font-weight: 800;
            letter-spacing: -0.5px;
            color: #ffffff;
        }

        .nav-right {
            display: flex;
            align-items: center;
            gap: 30px;
        }

        .nav-links a {
            color: var(--text-main);
            text-decoration: none;
            font-weight: 600;
            font-size: 0.95rem;
            margin-left: 20px;
            transition: color 0.2s;
        }

        .nav-links a:hover {
            color: var(--accent-blue);
        }

        .lang-switch {
            display: flex;
            background: #1c1c1e;
            border-radius: 30px;
            padding: 4px;
            gap: 2px;
        }

        .lang-btn {
            background: transparent;
            border: none;
            color: var(--text-muted);
            padding: 6px 14px;
            font-weight: 700;
            font-size: 0.8rem;
            border-radius: 20px;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .lang-btn.active {
            background-color: #333333;
            color: #ffffff;
        }

        .hero {
            max-width: 1200px;
            margin: 80px auto 100px auto;
            padding: 0 20px;
            text-align: center;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .hero img.main-logo {
            width: 120px;
            height: 120px;
            margin-bottom: 30px;
            border-radius: 28px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.8);
        }

        .hero h2 {
            font-size: 5rem;
            font-weight: 800;
            margin-bottom: 24px;
            line-height: 1.05;
            letter-spacing: -2px;
            max-width: 900px;
        }

        .hero h2 span {
            background: linear-gradient(135deg, #ffffff 0%, #aaaaaa 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .hero p {
            font-size: 1.5rem;
            color: var(--text-muted);
            max-width: 700px;
            margin: 0 auto 40px auto;
            font-weight: 400;
        }

        .badges {
            display: flex;
            justify-content: center;
            gap: 12px;
            flex-wrap: wrap;
            margin-bottom: 50px;
        }

        .badge {
            background: #1c1c1e;
            padding: 8px 18px;
            border-radius: 30px;
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--text-main);
            border: 1px solid #333;
        }

        .cta-buttons {
            display: flex;
            justify-content: center;
            gap: 20px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 16px 32px;
            border-radius: 30px;
            font-weight: 700;
            font-size: 1.1rem;
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background-color: var(--text-main);
            color: #000000;
        }

        .btn-primary:hover {
            transform: scale(1.05);
            background-color: #f0f0f0;
        }

        .btn-secondary {
            background-color: transparent;
            color: var(--text-main);
            border: 1px solid #555;
        }
        
        .btn-secondary:hover {
            background-color: #222;
            border-color: #888;
        }

        /* SECTION TITLES */
        .section-title {
            text-align: center;
            font-size: 3.5rem;
            font-weight: 800;
            margin-bottom: 50px;
            letter-spacing: -1.5px;
        }

        /* GALLERY SECTION */
        .gallery-section {
            max-width: 1200px;
            margin: 0 auto 120px auto;
            padding: 0 20px;
        }

        .gallery-title {
            text-align: center;
            font-size: 3.5rem;
            font-weight: 800;
            margin-bottom: 50px;
            letter-spacing: -1.5px;
        }

        .gallery-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 30px;
        }

        .gallery-card {
            background: var(--bg-surface);
            border-radius: 24px;
            padding: 20px;
            text-align: center;
            transition: transform 0.3s ease;
            border: 1px solid rgba(255,255,255,0.05);
        }

        .gallery-card:hover {
            transform: translateY(-8px);
            background: #1a1a1a;
        }

        .gallery-card img {
            width: 100%;
            height: auto;
            border-radius: 16px;
            margin-bottom: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
        }

        .gallery-card p {
            font-size: 0.95rem;
            font-weight: 600;
            color: var(--text-main);
        }

        /* FLAGSHIP ALMA MATER SECTION */
        .flagship-section {
            max-width: 1200px;
            margin: 0 auto 120px auto;
            padding: 0 20px;
        }

        .flagship-card {
            background: var(--bg-surface);
            border-radius: 32px;
            padding: 80px 60px;
            position: relative;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
            border: 1px solid rgba(255,255,255,0.05);
        }

        .flagship-badge {
            display: inline-block;
            background-color: rgba(255,255,255,0.1);
            color: #fff;
            font-size: 0.9rem;
            font-weight: 700;
            padding: 8px 24px;
            border-radius: 30px;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 30px;
        }

        .flagship-card h3 {
            font-size: 4rem;
            font-weight: 800;
            margin-bottom: 24px;
            letter-spacing: -2px;
        }

        .flagship-card p.lead {
            font-size: 1.4rem;
            color: var(--text-muted);
            margin-bottom: 60px;
            max-width: 800px;
        }

        .flagship-features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 24px;
            width: 100%;
            text-align: left;
        }

        .feature-item {
            background: #151515;
            border-radius: 20px;
            padding: 32px;
            border: 1px solid rgba(255,255,255,0.03);
            transition: background 0.3s ease;
        }
        
        .feature-item:hover {
            background: #1a1a1a;
        }

        .feature-item h4 {
            font-size: 1.3rem;
            font-weight: 700;
            color: #fff;
            margin-bottom: 12px;
        }

        .feature-item p {
            font-size: 1.05rem;
            color: var(--text-muted);
            line-height: 1.6;
        }

        .features {
            max-width: 1200px;
            margin: 0 auto 120px auto;
            padding: 0 20px;
        }

        .detailed-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 32px;
        }

        @media (max-width: 768px) {
            .detailed-grid {
                grid-template-columns: 1fr;
            }
            .hero h2 { font-size: 3.5rem; letter-spacing: -1.5px; }
            .flagship-card h3 { font-size: 3rem; }
            .section-title { font-size: 2.8rem; }
        }

        .detailed-card {
            background-color: var(--bg-surface);
            border-radius: 28px;
            padding: 48px;
            transition: transform 0.3s ease, background 0.3s ease;
            border: 1px solid rgba(255,255,255,0.05);
        }

        .detailed-card:hover {
            transform: translateY(-5px);
            background-color: #151515;
        }

        .detailed-card-header {
            display: flex;
            align-items: center;
            gap: 24px;
            margin-bottom: 24px;
        }

        .detailed-card-icon {
            font-size: 2.5rem;
            background: #222;
            padding: 20px;
            border-radius: 24px;
        }

        .detailed-card h3 {
            font-size: 1.8rem;
            font-weight: 800;
            letter-spacing: -0.5px;
        }

        .detailed-card p.desc {
            color: var(--text-muted);
            font-size: 1.1rem;
            margin-bottom: 32px;
            line-height: 1.6;
        }

        .feature-bullets {
            list-style: none;
        }

        .feature-bullets li {
            position: relative;
            padding-left: 32px;
            margin-bottom: 16px;
            font-size: 1.05rem;
            color: #ccc;
        }

        .feature-bullets li::before {
            content: "→";
            position: absolute;
            left: 0;
            color: #555;
            font-weight: bold;
        }

        .install-section {
            max-width: 800px;
            margin: 0 auto 120px auto;
            padding: 60px;
            background: var(--bg-surface);
            border-radius: 32px;
            border: 1px solid rgba(255,255,255,0.05);
        }

        .install-section h3 {
            font-size: 2.5rem;
            margin-bottom: 30px;
            font-weight: 800;
            letter-spacing: -1px;
        }

        .install-steps {
            list-style: decimal inside;
            color: var(--text-muted);
            font-size: 1.15rem;
            line-height: 2.2;
        }
        
        .install-steps li {
            margin-bottom: 20px;
        }

        footer {
            max-width: 1200px;
            margin: 0 auto;
            padding: 60px 20px;
            text-align: center;
            color: var(--text-muted);
            font-size: 1.05rem;
            border-top: 1px solid rgba(255,255,255,0.1);
        }

        footer a {
            color: var(--text-main);
            text-decoration: none;
            font-weight: 600;
        }
        
        footer a:hover {
            text-decoration: underline;
        }
"""

content = re.sub(r'<style>.*?</style>', f'<style>\n{new_css}\n    </style>', content, flags=re.DOTALL)

with open('c:/ALTGRAPH/docs/index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Updated index.html style successfully')
