import re

with open("docs/index.html", "r", encoding="utf-8") as f:
    html = f.read()

# 1. Add PT button
html = html.replace('<button onclick="setLanguage(\'de\')" id="btn-de" class="lang-btn">DE</button>',
                    '<button onclick="setLanguage(\'de\')" id="btn-de" class="lang-btn">DE</button>\n                <button onclick="setLanguage(\'pt\')" id="btn-pt" class="lang-btn">PT</button>')


# 2. Add m6 HTML block and fix m5 grid column
m6_html = """
            <div class="detailed-card">
                <div class="detailed-card-header">
                    <div class="detailed-card-icon">🗺️</div>
                    <div>
                        <h3 id="m6-title">GPS Isométrico Global</h3>
                        <span style="color: var(--accent-blue); font-size: 0.85rem; font-weight: 700;" id="m6-tag">Isometric GPS Map</span>
                    </div>
                </div>
                <p class="desc" id="m6-desc">Navegación espacial interactiva que proyecta la ruta completa como un mapa 3D con colores de altimetría.</p>
                <ul class="feature-bullets" id="m6-bullets">
                    <li><strong>Proyección Topográfica Real</strong>: Escala y ángulos GPS reales del mundo proyectados en una cuadrícula isométrica 3D.</li>
                    <li><strong>Baliza de Navegación 3D</strong>: Indicador dinámico que se mueve y rota a lo largo de la ruta marcando la posición exacta del ciclista.</li>
                    <li><strong>Gradiente Multiescala</strong>: Bloques de color integrados en la estructura 3D que delatan las ascensiones.</li>
                </ul>
            </div>"""

html = html.replace('class="detailed-card" style="grid-column: 1 / -1;"', 'class="detailed-card"')
html = html.replace('</ul>\n            </div>\n        </div>\n    </section>', '</ul>\n            </div>\n' + m6_html + '\n        </div>\n    </section>')


# 3. Replace 5 Modelos -> 6 Modelos
html = html.replace("Suite de 5 modelos", "Suite de 6 modelos")
html = html.replace("Suite de 5 Modelos", "Suite de 6 Modelos")
html = html.replace("Suite of 5 Models", "Suite of 6 Models")
html = html.replace("Suite de 5 Modèles", "Suite de 6 Modèles")
html = html.replace("Suite di 5 Modelli", "Suite di 6 Modelli")
html = html.replace("Suite von 5 Modellen", "Suite von 6 Modellen")

html = html.replace("5 modelos de altimetría", "6 modelos de altimetría")
html = html.replace("5 modelos ǧnicos", "6 modelos únicos")
html = html.replace("5 modelos únicos", "6 modelos únicos")
html = html.replace("5 unique models", "6 unique models")
html = html.replace("5 modèles uniques", "6 modèles uniques")
html = html.replace("5 modelli unici", "6 modelli unici")
html = html.replace("5 einzigartigen Modellen", "6 einzigartigen Modellen")

# 4. We need to add m6 translations for JS updates
# First, let's just make sure all JS translation blocks have m6
js_update_m6 = {
    'es': """                m6Title: "GPS Isométrico Global",
                m6Tag: "Isometric GPS Map",
                m6Desc: "Navegación espacial interactiva que proyecta la ruta completa como un mapa 3D con colores de altimetría.",
                m6Bullets: `<li><strong>Proyección Topográfica Real</strong>: Escala y ángulos GPS reales del mundo proyectados en una cuadrícula isométrica 3D.</li><li><strong>Baliza de Navegación 3D</strong>: Indicador dinámico que se mueve y rota a lo largo de la ruta marcando la posición exacta del ciclista.</li><li><strong>Gradiente Multiescala</strong>: Bloques de color integrados en la estructura 3D que delatan las ascensiones.</li>`,
""",
    'en': """                m6Title: "Global Isometric GPS",
                m6Tag: "Isometric GPS Map",
                m6Desc: "Interactive spatial navigation that projects the entire route as a 3D map with altimetry colors.",
                m6Bullets: `<li><strong>Real Topographic Projection</strong>: Real-world GPS scale and angles projected on a 3D isometric grid.</li><li><strong>3D Navigation Beacon</strong>: Dynamic indicator that moves and rotates along the route, marking the exact position of the cyclist.</li><li><strong>Multiscale Gradient</strong>: Color blocks integrated into the 3D structure that reveal climbs.</li>`,
""",
    'fr': """                m6Title: "GPS Isométrique Global",
                m6Tag: "Isometric GPS Map",
                m6Desc: "Navigation spatiale interactive qui projette l'itinéraire complet sous forme de carte 3D avec des couleurs d'altimétrie.",
                m6Bullets: `<li><strong>Projection Topographique Réelle</strong>: Échelle et angles GPS du monde réel projetés sur une grille isométrique 3D.</li><li><strong>Balise de Navigation 3D</strong>: Indicateur dynamique qui se déplace et pivote le long de l'itinéraire.</li><li><strong>Gradient Multi-échelle</strong>: Blocs de couleur intégrés dans la structure 3D révélant les ascensions.</li>`,
""",
    'it': """                m6Title: "GPS Isometrico Globale",
                m6Tag: "Isometric GPS Map",
                m6Desc: "Navigazione spaziale interattiva che proietta l'intero percorso come una mappa 3D con colori altimetrici.",
                m6Bullets: `<li><strong>Proiezione Topografica Reale</strong>: Scala e angoli GPS del mondo reale proiettati su una griglia isometrica 3D.</li><li><strong>Faro di Navigazione 3D</strong>: Indicatore dinamico che si muove e ruota lungo il percorso.</li><li><strong>Gradiente Multiscala</strong>: Blocchi di colore integrati nella struttura 3D che rivelano le salite.</li>`,
""",
    'de': """                m6Title: "Globales Isometrisches GPS",
                m6Tag: "Isometric GPS Map",
                m6Desc: "Interaktive räumliche Navigation, die die gesamte Route als 3D-Karte mit Höhenfarben projiziert.",
                m6Bullets: `<li><strong>Echte topografische Projektion</strong>: Reale GPS-Maßstäbe und -Winkel auf ein isometrisches 3D-Raster projiziert.</li><li><strong>3D-Navigationsbake</strong>: Dynamischer Indikator, der sich entlang der Route bewegt und dreht.</li><li><strong>Mehrstufiger Farbverlauf</strong>: Farbblöcke in der 3D-Struktur integriert, die Anstiege aufzeigen.</li>`,
"""
}

# The javascript structure is:
#                 m5Bullets: `...`,
#                 secFieldsTitle: ...
# So we can replace `m5Bullets: \`...\`,` with `m5Bullets: \`...\`,\n{m6}`

matches = list(re.finditer(r'(m5Bullets:\s*`.*?`,)', html, flags=re.DOTALL))
if len(matches) >= 5:
    html = html[:matches[0].end()] + "\n" + js_update_m6['es'] + html[matches[0].end():matches[1].end()] + "\n" + js_update_m6['en'] + html[matches[1].end():matches[2].end()] + "\n" + js_update_m6['fr'] + html[matches[2].end():matches[3].end()] + "\n" + js_update_m6['it'] + html[matches[3].end():matches[4].end()] + "\n" + js_update_m6['de'] + html[matches[4].end():]


# Update `setLanguage(lang)` function
setLangReplacement = """
            if (document.getElementById('m5-desc')) document.getElementById('m5-desc').innerHTML = t.m5Desc;
            if (document.getElementById('m5-bullets')) document.getElementById('m5-bullets').innerHTML = t.m5Bullets;

            if (document.getElementById('m6-title')) document.getElementById('m6-title').innerText = t.m6Title;
            if (document.getElementById('m6-tag')) document.getElementById('m6-tag').innerText = t.m6Tag;
            if (document.getElementById('m6-desc')) document.getElementById('m6-desc').innerHTML = t.m6Desc;
            if (document.getElementById('m6-bullets')) document.getElementById('m6-bullets').innerHTML = t.m6Bullets;
"""
html = re.sub(r'if \(document\.getElementById\(\'m5-bullets\'\)\) document\.getElementById\(\'m5-bullets\'\)\.innerHTML = t\.m5Bullets;', setLangReplacement, html)


# Add the PT translation block entirely. 
# Look for the end of 'de' block which is `        };` before `function setLanguage`
pt_translation = """
            pt: {
                pageTitle: "ALTGRAPH v0.7.0 ELITE - Extensão de Altimetria 3D e Desempenho por David García Pascual",
                navVideo: "Demonstração",
                navGallery: "Capturas",
                navModels: "Modelos 3D",
                navFields: "Campos",
                navInstall: "Instalação",
                heroTitle: "Domine a montanha com <span>ALTGRAPH v0.7.0 ELITE</span>",
                heroDesc: "Extensão avançada desenvolvida por <strong>David García Pascual</strong>. Suite de <strong>6 Modelos de Altimetria 3D</strong> alternáveis em tempo real, gradiente monocromático suave de 15 níveis, avanço quântico com janela rolante de 50 metros, cálculo topográfico, fontes Google Sans Condensed, modo paisagem girado a 90°, alertas de ataque, <strong>Ritmo VAM</strong> e cálculo científico do <strong>Grau de Fadiga (GF)</strong>.",
                btnDownload: "Baixar APK (v0.7.0)",
                btnManual: "Manual do Usuário",
                videoLabel: "🎥 VÍDEO DEMO 4K: Interface e Navegação Híbrida 3D em tempo real",
                galleryMainTitle: "📱 Capturas em Tela Real no Karoo",
                g1Label: "Visualização 3D - 200m",
                g2Label: "Dashboard Completo",
                g3Label: "Visor de Subidas - HC",
                g4Label: "Visor de Subidas - Isométrico",
                g5Label: "Modo Paisagem",
                flagshipBadge: "Exclusivo ALTGRAPH",
                flagshipTitle: "Novo Visor de Subidas de Alta Resolução",
                flagshipLead: "O <strong>Climb Viewer</strong> revoluciona a maneira de enfrentar as passagens de montanha no Hammerhead Karoo. Totalmente programado a partir do zero para um desempenho excepcional e detalhe topográfico.",
                f1Title: "Categorização Científica da Montanha (HC a 4ª Cat)",
                f1Desc: "Com base no Gráfico de Esforço APM, cada subida é classificada automaticamente. Mostraremos categorias clássicas da Vuelta a España.",
                f2Title: "Rastreamento Visual do Gradiente",
                f2Desc: "Identifique rapidamente a dificuldade do terreno com nossa paleta cromática inteligente que vai de verde/azul para descanso, amarelo para trechos falsos, até lavajato/obsidiana para muros brutais.",
                f3Title: "Navegação por Subidas",
                f3Desc: "Botões virtuais na tela permitem trocar de subida facilmente, verificando perfil, rampas e características perfeitamente adaptadas ao tamanho do ecrã.",
                f4Title: "Janela Adaptativa e Suavização Inteligente",
                f4Desc: "A renderização resolve o excesso de micro-irregularidades na telemetria, oferecendo uma forma suave e contínua do perfil do cume sem perder rampas curtas críticas.",
                f5Title: "Indicador Inteligente 3D",
                f5Desc: "A sua posição exata na subida está perfeitamente rastreada ao longo do caminho, mesmo em visualizações isométricas.",
                f6Title: "Desempenho Extremo a 60 FPS",
                f6Desc: "Reescrito desde zero usando canvas e reutilização inteligente de bitmap. Esqueça quedas de quadros durante subidas complexas.",
                secModelsTitle: "6 Modelos de Altimetria Revolucionários",
                secModelsDesc: "Revoluciona a clássica barra de elevação plana com 6 modelos únicos que você pode escolher a qualquer momento no painel de configurações do Karoo:",
                m1Title: "Clássico 3D (Padrão)",
                m1Tag: "Classic 3D Profile",
                m1Desc: "Perfil profissional de relevo 3D suave, usando nossa progressão monocromática contínua de 15 passos de 0 a 15% (Branco a Vermelho), Vermelho Profundo para 15-20%, Preto total para >20% e Azul para descidas.",
                m1Bullets: `<li><strong>Gradiente Monocromático</strong>: Escala contínua de 15 faixas com precisão de 1% que evita saltos visuais abruptos.</li><li><strong>Cápsulas de Alto Contraste</strong>: Algarismos grandes em pílulas escuras para ótima leitura ao sol.</li><li><strong>Cotas Verticais em Linha</strong>: Impressão vertical exata dos medidores de elevação com os separadores de escala 90°.</li>`,
                m2Title: "Horizonte Isométrico",
                m2Tag: "Cockpit 3D Horizon",
                m2Desc: "Perspectiva de cabine apontando para o infinito com uma linha central tracejada semelhante à de um aeroporto e focos interativos.",
                m2Bullets: `<li><strong>Farol de Projeção Inclinada</strong>: Feixe de luz sob o ciclista que detecta e ilumina a rampa futura imediata.</li><li><strong>Convergência Isométrica</strong>: Profundidade 3D criada estreitando continuamente as pistas da estrada até a distância.</li><li><strong>Borda de Gelo Azul</strong>: Desenho limpo de recorte contra um fundo negro.</li>`,
                m3Title: "Oásis Táticos e Forno Quente",
                m3Tag: "Tactical Recovery & Fire",
                m3Desc: "Modelo tático para corridas focando áreas de recuperação fáceis vs segmentos cruciais de quebra-pernas.",
                m3Bullets: `<li><strong>Oásis de Oxigênio</strong>: Áreas de descanso (≤3%) em azul ciano de gelo estourando uma bandeira de banner <code>❄️ OÁSIS [dist]m</code>.</li><li><strong>Fornos de Fogo Ardente</strong>: Rampas severas (≥12%) em lava térmica mostrando bandeira de perigo <code>🔥 MURO [dist]m</code>.</li><li><strong>Névoa de Hipóxia Ativada</strong>: Adiciona um efeito atmosférico de fumaça cinza desbotada em picos de alta montanha >1.400 metros de elevação.</li>`,
                m4Title: "Campo de Força Gravitacional",
                m4Tag: "Kinetic Gravitational Field",
                m4Desc: "Dashboard de telemetria onde inércia cinética, densidade de massa e forças gravitacionais se visualizam.",
                m4Bullets: `<li><strong>Tensão Gravitacional</strong>: Puxões e âncoras verticais presas precisamente quando as rampas ultrapassam 8%.</li><li><strong>Onda Cinética de Inércia</strong>: Uma onda senoidal ondulante rápida (ciano fluido em bom impulso vs carmesim rígido em desaceleração forte de rampa).</li><li><strong>Linha de Pacing VAM Suspensa</strong>: Pista de ritmo guiada dourada pairando 12px acima para sincronizar seu esforço sem sobressaltos.</li>`,
                m5Title: "Monólito de Obsidiana Facetado",
                m5Tag: "Monolithic Obsidian & Radiant Plasma",
                m5Desc: "Uma escultura montanhosa geométrica em vidro âmbar enfumaçado 3D contendo um núcleo térmico brilhante encapsulado sob vigas a laser em bordas de neon.",
                m5Bullets: `<li><strong>Rocha Diamante Obsidiana</strong>: Construção pesada em escuro com texturas reflexivas brilhantes usando técnica de recorte 3D de 45 graus.</li><li><strong>Núcleo Radiante Térmico de Plasma</strong>: Sangramento interno centralizado em rampa-estrita brilhando a temperatura codificada por cores reais.</li><li><strong>Linha de Borda Laser-Neon</strong>: Crista dupla perfeitamente focada (brilho ciano suave de 6px sob pico branco puro sólido de 2,2px).</li>`,
                m6Title: "GPS Isométrico Global",
                m6Tag: "Isometric GPS Map",
                m6Desc: "Navegação espacial interativa que projeta a rota completa como um mapa 3D com cores de altimetria.",
                m6Bullets: `<li><strong>Projeção Topográfica Real</strong>: Escala e ângulos GPS do mundo real projetados numa grelha isométrica 3D.</li><li><strong>Sinalizador de Navegação 3D</strong>: Indicador dinâmico que se move e roda ao longo da rota.</li><li><strong>Gradiente Multiescala</strong>: Blocos de cor integrados na estrutura 3D revelando subidas.</li>`,
                secFieldsTitle: "Análise Profunda de 4 Campos Adicionais",
                c2Title: "Estrategista de Elevação 2D",
                c2Tag: "2D Profile & Strategy",
                c2Desc: "Visão estratégica usando um perfil de bloco distante em lote que detecta paredes repentinas emitindo alertas de pop-up.",
                c2Bullets: `<li><strong>Configuração de Bloco Rápida</strong>: Escolha rastrear rapidamente 1 a 10 blocos na tela (5 por padrão).</li><li><strong>Resolução de Recarga Distante</strong>: Defina a resolução macro do horizonte para atualizar a cada 50m, 100m, 250m, 500m ou 1km.</li><li><strong>Impresso a Laser</strong>: Porcentagens lidas diretamente na face dos preenchimentos poligonais brutos.</li><li><strong>Alerta de Combate!</strong>: Gatilhos automáticos na tela piscando quando o bloco de destino relata inclinação de 10%+.</li><li><strong>Rastreamento de Meta</strong>: Distância superior, tempo estimado do cume e velocidade média da parede para cruzar picos perfeitamente.</li>`,
                c3Title: "Grau de Fadiga da Montanha (GF)",
                c3Tag: "Scientific Climb Hardness",
                c3Desc: "Avaliador e classificador contínuo em tempo real indexado para desgaste atlético que lida com rugosidade do asfalto, dor severa de rampa e inclinações em andamento.",
                c3Bullets: `<li><strong>Modelo APM Restrito Pesado</strong>: Multiplicador DU pesado cruzando distâncias em agrupamentos base (0-3%, 3-9%, 9-20%, >20%).</li><li><strong>Alternador de Atrito de Asfalto (TA)</strong>: Configuração interna com Muito Bom (0,1), Bom (0,5), Médio (1,2) ou Cascalho Sujo e Desgastado (1,7).</li><li><strong>Compensação do Sofredor Pmx</strong>: Injeção matemática que lida com surtos de inclinação repentina de dor dividindo rampas de pico com punição fixa adicional.</li><li><strong>Juiz Tour Pro Classificado</strong>: Saída instantânea que o classifica como HC Categoria Especial ao longo do caminho, Categoria 1 a 5 ou colinas não pontuáveis.</li>`,
                c4Title: "Assistente Gêmeo de Alvo VAM (A/B/C)",
                c4Tag: "Dual Pacing Assistant",
                c4Desc: "Sistema de gerenciamento de cruzeiro assistido que processa seu VAM alvo projetando em que exata 'Velocidade Kmh' de giro rodoviário contínua se baseia sua velocidade real atual de montanha.",
                c4Bullets: `<li><strong>Placard Telemetria VAM Duplo</strong>: Rastreamento em tela em grande velocidade Vertical Ascendente (ex. 850m/h) contra o alvo VAM emparelhado bloqueado.</li><li><strong>Injeção Recomendada Kmh/Mph</strong>: Matemática traduz sua inclinação exata sob seus pneus recomendando você girar em velocidade plana cruzeiro alvo Kmh equivalente de dor!</li><li><strong>Guia Tático de Pílula Colorida</strong>: Sem adivinhação, bolhas dizem para você empurrar 🔴 RÁPIDO DEMAIS, segurar 🟢 NO RITMO, ou girar agressivo 🔵 MUITO LENTO.</li><li><strong>Flexibilidade Total do Ciclista</strong>: Você diz no aplicativo Karoo se sua mira VAM relaxa para 500m/h subindo até atletas de 2000m/h Pro Tour.</li>`,
                c5Title: "Predição 3D + Memória Máxima Pmx",
                c5Tag: "Gradient Trend Tracker",
                c5Desc: "Sistema reativo imediato construído ignorando atrasos barométricos capturando instantâneos da vida real e previsões do que sua corrente sofrerá a seguir.",
                c5Bullets: `<li><strong>Sistema Vetorial Previsor</strong>: Adição micro-radar estocástico prevendo matematicamente as rampas com antecedência usando cálculo visual do mapa interno 3D.</li><li><strong>Faróis Direcionais HUD</strong>: Ícones de visualização apontam setas ativas se o terreno ↗️ ENDURECE mais fundo, bloqueia cruzamento plano ➔ CONSTANTE ou relaxa alivio ↘️ ALIVIA no topo.</li><li><strong>Monitor Pmx de Registro Permanente</strong>: Bloqueia na tela o pico mais feio registrado em Kmh/Mph sem desaparecer mesmo após você girar fora dele. O Karoo nativo se esquece rápido.</li>`,
                installTitle: "🚀 Implantação e Instalação Tática Rápida (Karoo 2/3)",
                installSteps: `<li>Vincule fisicamente sua arma de plataforma de ciclismo Hammerhead a qualquer unidade cibernética PC através do cabo USB com alternância inteligente avançada <strong>Depuração USB</strong> ativada.</li><li>Puxe o lançamento da carga do arquivo principal <code>ALTGRAPH-v0.7.0.apk</code> do cofre do nosso repositório no GitHub.</li><li>Execute comandos vitais simples do prompt shell do sistema: <code>adb install -r ALTGRAPH-v0.7.0.apk</code></li><li>Dirija o painel em voo do seu dispositivo real Karoo local e monte o campo cibernético atômico de Altimetria ou VAM da categoria listada dedicada <strong>ALTGRAPH</strong> usando <strong>Perfis de Ciclismo -> Modificar Páginas de Dados</strong>.</li><li>Implante totalmente suas cargas modificadas nas <strong>Configurações do aplicativo Karoo App Launcher</strong>. Todas as engrenagens são operacionais sob demanda de tela sem toques sem fio na rede.</li>`,
                footerText: `ALTGRAPH v0.7.0 ELITE &bull; Desenvolvido por David García Pascual &bull; <a href="https://github.com/DAVOE75/ALTGRAPH" target="_blank">Repositório Oficial do GitHub Open Source</a>`
            },
"""

# Find the end of 'de' to insert 'pt' before closing translations
# 'de' is the last one in the dict. So replacing `    };` that closes `translations` with `    },` + pt + `    };`
html = re.sub(r'(\s*de: {[\s\S]*?)(};)', r'\1,\n' + pt_translation + r'        };', html)

with open("docs/index.html", "w", encoding="utf-8") as f:
    f.write(html)
print("Updated docs/index.html")
