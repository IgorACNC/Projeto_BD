package servlets;

import dao.JogadorDAO;
import dao.TimeDAO;
import model.Jogador;
import model.Time;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class JogadorServlet extends HttpServlet {
    private JogadorDAO jogadorDAO = new JogadorDAO();
    private TimeDAO timeDAO = new TimeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                listarJogadores(request, response);
            } else if (action.equals("form")) {
                mostrarFormulario(request, response);
            } else if (action.equals("edit")) {
                mostrarFormularioEdicao(request, response);
            } else if (action.equals("delete")) {
                excluirJogador(request, response);
            } else if (action.equals("relatorio")) {
                relatorioCompleto(request, response);
            } else if (action.equals("artilheiros")) {
                relatorioArtilheiros(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action.equals("create")) {
                criarJogador(request, response);
            } else if (action.equals("update")) {
                atualizarJogador(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }

    private void listarJogadores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<Jogador> jogadores = jogadorDAO.listar();
        request.setAttribute("jogadores", jogadores);
        request.getRequestDispatcher("/WEB-INF/views/jogador/listar.jsp").forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<Time> times = timeDAO.listar();
        request.setAttribute("times", times);
        request.getRequestDispatcher("/WEB-INF/views/jogador/formulario.jsp").forward(request, response);
    }

    private void mostrarFormularioEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        int id = Integer.parseInt(request.getParameter("id"));
        Jogador jogador = jogadorDAO.buscarPorId(id);
        List<Time> times = timeDAO.listar();

        request.setAttribute("jogador", jogador);
        request.setAttribute("times", times);
        request.getRequestDispatcher("/WEB-INF/views/jogador/editar.jsp").forward(request, response);
    }

    private void criarJogador(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        Jogador jogador = new Jogador();
        jogador.setNome(request.getParameter("nome"));
        jogador.setIdade(Integer.parseInt(request.getParameter("idade")));
        jogador.setAltura(Float.parseFloat(request.getParameter("altura")));
        jogador.setNumero_camisa(Integer.parseInt(request.getParameter("numero_camisa")));
        jogador.setQuant_times_jogados(Integer.parseInt(request.getParameter("quant_times_jogados")));
        jogador.setGols_temporada_jogador(Integer.parseInt(request.getParameter("gols_temporada_jogador")));
        jogador.setPe_dominante(request.getParameter("pe_dominante"));
        jogador.setGols_penalti(Integer.parseInt(request.getParameter("gols_penalti")));
        jogador.setGols_cabeca(Integer.parseInt(request.getParameter("gols_cabeca")));
        jogador.setPeso(Float.parseFloat(request.getParameter("peso")));
        jogador.setPosicao_jogador(request.getParameter("posicao_jogador"));
        jogador.setQuant_jogos(Integer.parseInt(request.getParameter("quant_jogos")));
        jogador.setNacionalidade(request.getParameter("nacionalidade"));
        jogador.setAssistencias(Integer.parseInt(request.getParameter("assistencias")));
        jogador.setFk_time(Integer.parseInt(request.getParameter("fk_time")));

        jogadorDAO.inserir(jogador);
        response.sendRedirect("jogadores?action=list");
    }

    private void atualizarJogador(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        Jogador jogador = new Jogador();
        jogador.setId_jogador(Integer.parseInt(request.getParameter("id")));
        jogador.setNome(request.getParameter("nome"));
        jogador.setIdade(Integer.parseInt(request.getParameter("idade")));
        jogador.setAltura(Float.parseFloat(request.getParameter("altura")));
        jogador.setNumero_camisa(Integer.parseInt(request.getParameter("numero_camisa")));
        jogador.setQuant_times_jogados(Integer.parseInt(request.getParameter("quant_times_jogados")));
        jogador.setGols_temporada_jogador(Integer.parseInt(request.getParameter("gols_temporada_jogador")));
        jogador.setPe_dominante(request.getParameter("pe_dominante"));
        jogador.setGols_penalti(Integer.parseInt(request.getParameter("gols_penalti")));
        jogador.setGols_cabeca(Integer.parseInt(request.getParameter("gols_cabeca")));
        jogador.setPeso(Float.parseFloat(request.getParameter("peso")));
        jogador.setPosicao_jogador(request.getParameter("posicao_jogador"));
        jogador.setQuant_jogos(Integer.parseInt(request.getParameter("quant_jogos")));
        jogador.setNacionalidade(request.getParameter("nacionalidade"));
        jogador.setAssistencias(Integer.parseInt(request.getParameter("assistencias")));
        jogador.setFk_time(Integer.parseInt(request.getParameter("fk_time")));

        jogadorDAO.atualizar(jogador);
        response.sendRedirect("jogadores?action=list");
    }

    private void excluirJogador(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        int id = Integer.parseInt(request.getParameter("id"));
        jogadorDAO.excluir(id);
        response.sendRedirect("jogadores?action=list");
    }

    private void relatorioCompleto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<String[]> relatorio = jogadorDAO.listarComInformacoesDoTime();
        request.setAttribute("relatorio", relatorio);
        request.getRequestDispatcher("/WEB-INF/views/jogador/relatorio.jsp").forward(request, response);
    }

    private void relatorioArtilheiros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<String[]> artilheiros = jogadorDAO.buscarArtilheiros();
        request.setAttribute("artilheiros", artilheiros);
        request.getRequestDispatcher("/WEB-INF/views/jogador/artilheiros.jsp").forward(request, response);
    }
}
