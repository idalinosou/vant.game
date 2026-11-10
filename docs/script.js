const DIFICULDADES = {
  FACIL: { rotulo: "Fácil", minimo: 1, maximo: 50, tentativas: 12, base: 150 },
  MEDIO: { rotulo: "Médio", minimo: 1, maximo: 100, tentativas: 8, base: 300 },
  DIFICIL: { rotulo: "Difícil", minimo: 1, maximo: 500, tentativas: 9, base: 500 },
};

const CHAVE_PARTIDAS = "vant.game.partidas.v1";
const CHAVE_NOME = "vant.game.nome";

const el = {
  telaInicio: document.getElementById("telaInicio"),
  telaJogo: document.getElementById("telaJogo"),
  nome: document.getElementById("nome"),
  comecar: document.getElementById("comecar"),
  avisoInicio: document.getElementById("avisoInicio"),
  dificuldadeAtual: document.getElementById("dificuldadeAtual"),
  tentativasAtual: document.getElementById("tentativasAtual"),
  tempoAtual: document.getElementById("tempoAtual"),
  intervalo: document.getElementById("intervalo"),
  formPalpite: document.getElementById("formPalpite"),
  palpite: document.getElementById("palpite"),
  botaoEnviar: document.querySelector("#formPalpite .botao"),
  dica: document.getElementById("dica"),
  barraProgresso: document.getElementById("barraProgresso"),
  acoesFim: document.getElementById("acoesFim"),
  jogarNovamente: document.getElementById("jogarNovamente"),
  trocarDificuldade: document.getElementById("trocarDificuldade"),
  listaRanking: document.getElementById("listaRanking"),
  rankingVazio: document.getElementById("rankingVazio"),
  rankingTotal: document.getElementById("rankingTotal"),
  limparRanking: document.getElementById("limparRanking"),
  ano: document.getElementById("ano"),
};

let partida = null;
let cronometro = null;

function carregarPartidas() {
  try {
    const bruto = localStorage.getItem(CHAVE_PARTIDAS);
    const lista = bruto ? JSON.parse(bruto) : [];
    return Array.isArray(lista) ? lista : [];
  } catch {
    return [];
  }
}

function salvarPartidas(partidas) {
  try {
    localStorage.setItem(CHAVE_PARTIDAS, JSON.stringify(partidas));
  } catch {
    el.rankingTotal.textContent = "Não foi possível salvar o ranking neste navegador.";
  }
}

function calcularPontos(config, tentativasUsadas, segundos) {
  const bonusTentativas = Math.max(0, config.tentativas - tentativasUsadas) * 10;
  const bonusTempo = Math.max(0, 30 - segundos);
  return Math.max(10, config.base + bonusTentativas + bonusTempo);
}

function sortear(config) {
  return Math.floor(Math.random() * (config.maximo - config.minimo + 1)) + config.minimo;
}

function formatarTempo(segundos) {
  const minutos = Math.floor(segundos / 60);
  const resto = segundos % 60;
  return minutos > 0 ? `${minutos}m ${resto}s` : `${resto}s`;
}

function iniciarCronometro() {
  clearInterval(cronometro);
  cronometro = setInterval(() => {
    if (!partida || !partida.ativa) return;
    el.tempoAtual.textContent = formatarTempo(segundosDecorridos());
  }, 250);
}

function segundosDecorridos() {
  return Math.max(1, Math.floor((Date.now() - partida.inicio) / 1000));
}

function atualizarPainel() {
  if (!partida) return;
  const config = partida.config;
  el.dificuldadeAtual.textContent = config.rotulo;
  el.tentativasAtual.textContent = `${partida.usadas}/${config.tentativas}`;
  el.tempoAtual.textContent = formatarTempo(segundosDecorridos());
  const progresso = (partida.usadas / config.tentativas) * 100;
  el.barraProgresso.style.width = `${progresso}%`;
}

function iniciarPartida(chave) {
  const config = DIFICULDADES[chave];
  const nome = el.nome.value.trim() || "jogador";

  try {
    localStorage.setItem(CHAVE_NOME, nome);
  } catch {
  }

  partida = {
    jogador: nome,
    chave,
    config,
    segredo: sortear(config),
    usadas: 0,
    inicio: Date.now(),
    ativa: true,
  };

  el.telaInicio.classList.add("escondido");
  el.telaJogo.classList.remove("escondido");
  el.acoesFim.classList.add("escondido");
  el.intervalo.textContent = `Pensei em um número entre ${config.minimo} e ${config.maximo}. Você tem ${config.tentativas} tentativas.`;
  el.dica.textContent = "";
  el.dica.className = "dica";
  el.palpite.value = "";
  el.palpite.disabled = false;
  el.botaoEnviar.disabled = false;
  el.avisoInicio.textContent = "";

  atualizarPainel();
  iniciarCronometro();
  el.palpite.focus();
}

function finalizar(venceu) {
  const config = partida.config;
  partida.ativa = false;
  clearInterval(cronometro);

  const segundos = segundosDecorridos();
  const pontos = venceu ? calcularPontos(config, partida.usadas, segundos) : 0;

  const registro = {
    jogador: partida.jogador,
    dificuldade: partida.chave,
    venceu,
    tentativas: partida.usadas,
    segredo: partida.segredo,
    segundos,
    pontos,
    data: new Date().toISOString(),
  };

  const partidas = carregarPartidas();
  partidas.push(registro);
  salvarPartidas(partidas);
  renderizarRanking();

  if (venceu) {
    el.dica.className = "dica acerto";
    el.dica.textContent = `Acertou, ${partida.jogador}! O número era ${partida.segredo}. +${pontos} pontos em ${partida.usadas} tentativa(s) e ${formatarTempo(segundos)}.`;
  } else {
    el.dica.className = "dica erro";
    el.dica.textContent = `Tentativas esgotadas. O número era ${partida.segredo}. Tente de novo!`;
  }

  el.palpite.disabled = true;
  el.botaoEnviar.disabled = true;
  el.acoesFim.classList.remove("escondido");
  el.jogarNovamente.focus();
  atualizarPainel();
}

function mostrarDica(tipo, valor) {
  if (tipo === "maior") {
    el.dica.className = "dica maior";
    el.dica.textContent = `O número é MAIOR que ${valor}.`;
  } else {
    el.dica.className = "dica menor";
    el.dica.textContent = `O número é MENOR que ${valor}.`;
  }
}

function validarPalpite(texto) {
  if (texto.trim() === "") {
    return "Digite um número antes de enviar.";
  }
  const valor = Number(texto);
  if (!Number.isInteger(valor)) {
    return "Digite um número inteiro, sem vírgulas.";
  }
  if (valor < partida.config.minimo || valor > partida.config.maximo) {
    return `Digite um número entre ${partida.config.minimo} e ${partida.config.maximo}.`;
  }
  return null;
}

function renderizarRanking() {
  const partidas = carregarPartidas();
  const vitorias = partidas
    .filter((registro) => registro.venceu && typeof registro.pontos === "number")
    .sort((a, b) => b.pontos - a.pontos || a.segundos - b.segundos)
    .slice(0, 5);

  el.listaRanking.innerHTML = "";

  vitorias.forEach((registro, indice) => {
    const item = document.createElement("li");

    const posicao = document.createElement("span");
    posicao.className = "posicao";
    posicao.textContent = `${indice + 1}º`;

    const jogador = document.createElement("span");
    jogador.className = "jogador";
    jogador.textContent = registro.jogador;

    const detalhe = document.createElement("small");
    const dificuldade = DIFICULDADES[registro.dificuldade];
    detalhe.textContent = `${dificuldade ? dificuldade.rotulo : registro.dificuldade} · ${registro.tentativas} tentativas · ${formatarTempo(registro.segundos)}`;
    jogador.appendChild(detalhe);

    const pontos = document.createElement("span");
    pontos.className = "pontos";
    pontos.textContent = `${registro.pontos} pts`;

    item.append(posicao, jogador, pontos);
    el.listaRanking.appendChild(item);
  });

  el.rankingVazio.classList.toggle("escondido", vitorias.length > 0);
  el.rankingTotal.textContent = partidas.length > 0
    ? `Partidas registradas: ${partidas.length}`
    : "";
}

el.comecar.addEventListener("click", () => {
  const escolhida = document.querySelector('input[name="dificuldade"]:checked');
  if (!escolhida) {
    el.avisoInicio.textContent = "Escolha uma dificuldade para começar.";
    return;
  }
  iniciarPartida(escolhida.value);
});

el.formPalpite.addEventListener("submit", (evento) => {
  evento.preventDefault();
  if (!partida || !partida.ativa) return;

  const erro = validarPalpite(el.palpite.value);
  if (erro) {
    el.dica.className = "dica erro";
    el.dica.textContent = erro;
    el.palpite.focus();
    return;
  }

  const palpite = Number(el.palpite.value);
  partida.usadas++;

  if (palpite === partida.segredo) {
    finalizar(true);
    return;
  }

  if (partida.usadas >= partida.config.tentativas) {
    finalizar(false);
    return;
  }

  mostrarDica(palpite < partida.segredo ? "maior" : "menor", palpite);
  el.palpite.value = "";
  atualizarPainel();
  el.palpite.focus();
});

el.jogarNovamente.addEventListener("click", () => {
  if (partida) iniciarPartida(partida.chave);
});

el.trocarDificuldade.addEventListener("click", () => {
  clearInterval(cronometro);
  partida = null;
  el.telaJogo.classList.add("escondido");
  el.telaInicio.classList.remove("escondido");
  el.comecar.focus();
});

el.limparRanking.addEventListener("click", () => {
  if (!window.confirm("Apagar todo o ranking deste navegador?")) return;
  try {
    localStorage.removeItem(CHAVE_PARTIDAS);
  } catch {
  }
  renderizarRanking();
});

function iniciar() {
  el.ano.textContent = new Date().getFullYear();
  try {
    const nomeSalvo = localStorage.getItem(CHAVE_NOME);
    if (nomeSalvo) el.nome.value = nomeSalvo;
  } catch {
  }
  renderizarRanking();
}

iniciar();
