<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="Logotipo ALTGRAPH" width="160" />
</p>
# ALTGRAPH (v0.7.0 ELITE)
<p>Ler em: <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README.md">🇪🇸 Español</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_en.md">🇬🇧 English</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_fr.md">🇫🇷 Français</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_it.md">🇮🇹 Italiano</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_de.md">🇩🇪 Deutsch</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_pt.md">🇵🇹 Português</a></p>

**ALTGRAPH** é uma extensão profissional de altimetria e desempenho de última geração para os ciclocomputadores **Hammerhead Karoo** (Karoo 2 e Karoo 3), desenvolvida por **David García Pascual** utilizando o SDK oficial `karoo-ext`.
Revoluciona o conceito tradicional de altimetria de ciclismo incorporando um **Conjunto de 6 Modelos de Visualização Altimétrica** com seletor dinâmico ao vivo, escala monocromática contínua de 15 níveis, avanço quântico com janela deslizante de 50 metros, resolução multiescala adaptável, análise de passagens de montanha, cálculo topográfico opcional, detecção matemática de curvas fechadas (tornanti), filtragem de Pontos de Interesse (POIs) e muito mais.

## 📸 Capturas de Tela no Karoo 3

| 🎨 Aba Estilo | ⚙️ Menu de Seleção | 📊 Dashboard Completo |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="Aba Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_data_selection.png" width="220" alt="Menu de Seleção" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_dashboard_completo.png" width="220" alt="Dashboard Completo" /> |

| ⛰️ Rota Global 3D (200m) | ⛰️ Rota Global 3D (1km) | ⛰️ Rota Global 3D (10km) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="Rota Global 3D 200m" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_1km.png" width="220" alt="Rota Global 3D 1km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_10km.png" width="220" alt="Rota Global 3D 10km" /> |

| ⛰️ Rota Global 3D (20km) | ⛰️ Rota Global 3D (200km) | ⛰️ Visor de Subidas - 3ª Cat |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="Rota Global 3D 20km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="Rota Global 3D 200km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer.png" width="220" alt="Visor de Subidas 3ª Cat" /> |

| ⛰️ Visor de Subidas - Especial C.E. | ⛰️ Visor de Subidas (Isométrico) | 🗺️ GPS Isométrico (Global) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="Visor de Subidas Especial CE" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_iso.png" width="220" alt="Visor de Subidas Isométrico" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_gps.png" width="220" alt="GPS Isométrico Global" /> |

---
## 🌐 Portal Web Oficial e Suporte Multilíngue (6 Idiomas)
Visite o **[Portal Web Oficial do ALTGRAPH (davoe75.github.io/ALTGRAPH)](https://davoe75.github.io/ALTGRAPH/)** para obter documentação interativa em espanhol, inglês, francês, italiano, alemão e português.

## ⚡ ALTGRAPH ELITE ⚡ (v0.7.0)
- **Strava Live Segments 3D (Beta):** Simulação ao vivo (KOM Ghost) em subidas desafiadoras.
- **Wind & Weather Overlay:** Integração nativa (Open-Meteo) para representar a direção do vento ao vivo usando vetores 3D (Vento a favor/Vento contra).

## 🚀 Novidades e Principais Funcionalidades (v0.7.0)
### ⛰️ Visor de Subidas 3D (NOVO na v0.4.5+)
A funcionalidade mais solicitada pela comunidade ciclista. Um **campo de dados em tela cheia** dedicado exclusivamente à visualização abrangente das subidas detectadas automaticamente em sua rota carregada:
- **Navegação de Subidas**: Setas `<` e `>` ampliadas para explorar todas as subidas da rota antes de sair ou durante a pedalada.
- **Informações da Subida**: Duração exata, ganho de elevação, quilômetros restantes até o início e categoria oficial.
- **Renderização Adaptável**: Escolha entre uma visualização isométrica 3D ou um perfil linear clássico para a subida, independente da rota global.
- **Novo**: Controles táteis de zoom e bússola configurável na Rota Global.

### 📈 Rota Global 3D
- Perfis clássicos lineares e renders isométricos 3D completos.
- Modo Paisagem giratório nativo para orientação adaptativa do Karoo.

### 🔋 Energy Management System
Sistema biomecânico para analisar a reserva de energia do ciclista.

### 🏅 Grau de Fadiga (GF)
Índice de dificuldade científica baseado em cálculos de resistência (Similar ao Coeficiente APM).

## 📥 Como Instalar e Usar
1. Baixe o APK mais recente de `Releases` no GitHub.
2. Sideload para o seu dispositivo Karoo usando as ferramentas de desenvolvimento do Hammerhead.
3. Adicione um novo Perfil ou Edite o seu atual. Adicione um novo campo de dados "Full Screen".
4. Selecione `ALTGRAPH` no menu de seleção e escolha uma das visualizações, como **Rota Global 3D** ou **Visor de Subidas 3D**.
5. Aproveite sua pedalada!

**Autor:** David García Pascual (DAVOE75)
**Licença:** MIT
