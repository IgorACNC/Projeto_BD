package servlets;

import dao.TimeDAO;
import dao.TecnicoDAO;
import dao.PresidenteDAO;
import dao.EstadioDAO;
import model.Time;
import model.Tecnico;
import model.Presidente;
import model.Estadio;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TimeServlet extends HttpServlet {
    private TimeDAO timeDAO = new TimeDAO();
    private TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private PresidenteDAO presidenteDAO = new PresidenteDAO();
    private EstadioDAO estadioDAO = new EstadioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                listarTimes(request, response);
            } else if (action.equals("form")) {
                mostrarFormulario(request, response);
            } else if (action.equals("edit")) {
                mostrarFormularioEdicao(request, response);
            } else if (action.equals("delete")) {
                excluirTime(request, response);
            } else if (action.equals("relatorio")) {
                relatorioCompleto(request, response);
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
                criarTime(request, response);
            } else if (action.equals("update")) {
                atualizarTime(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }

    private void listarTimes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<Time> times = timeDAO.listar();
        request.setAttribute("times", times);
        request.getRequestDispatcher("/WEB-INF/views/time/listar.jsp").forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<Tecnico> tecnicos = tecnicoDAO.listar();
        List<Presidente> presidentes = presidenteDAO.listar();
        List<Estadio> estadios = estadioDAO.listar();

        request.setAttribute("tecnicos", tecnicos);
        request.setAttribute("presidentes", presidentes);
        request.setAttribute("estadios", estadios);
        request.getRequestDispatcher("/WEB-INF/views/time/formulario.jsp").forward(request, response);
    }

    private void mostrarFormularioEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        int id = Integer.parseInt(request.getParameter("id"));
        Time time = timeDAO.buscarPorId(id);

        List<Tecnico> tecnicos = tecnicoDAO.listar();
        List<Presidente> presidentes = presidenteDAO.listar();
        List<Estadio> estadios = estadioDAO.listar();

        request.setAttribute("time", time);
        request.setAttribute("tecnicos", tecnicos);
        request.setAttribute("presidentes", presidentes);
        request.setAttribute("estadios", estadios);
        request.getRequestDispatcher("/WEB-INF/views/time/editar.jsp").forward(request, response);
    }

    private void criarTime(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        Time time = new Time();
        time.setNome(request.getParameter("nome"));
        time.setQuant_jogadores(Integer.parseInt(request.getParameter("quant_jogadores")));
        time.setQuant_socios(Integer.parseInt(request.getParameter("quant_socios")));
        time.setFk_tecnico(Integer.parseInt(request.getParameter("fk_tecnico")));
        time.setFk_presidente(Integer.parseInt(request.getParameter("fk_presidente")));
        time.setFk_estadio(Integer.parseInt(request.getParameter("fk_estadio")));

        timeDAO.inserir(time);
        response.sendRedirect("times?action=list");
    }

    private void atualizarTime(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        Time time = new Time();
        time.setId_time(Integer.parseInt(request.getParameter("id")));
        time.setNome(request.getParameter("nome"));
        time.setQuant_jogadores(Integer.parseInt(request.getParameter("quant_jogadores")));
        time.setQuant_socios(Integer.parseInt(request.getParameter("quant_socios")));
        time.setFk_tecnico(Integer.parseInt(request.getParameter("fk_tecnico")));
        time.setFk_presidente(Integer.parseInt(request.getParameter("fk_presidente")));
        time.setFk_estadio(Integer.parseInt(request.getParameter("fk_estadio")));

        timeDAO.atualizar(time);
        response.sendRedirect("times?action=list");
    }

    private void excluirTime(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        int id = Integer.parseInt(request.getParameter("id"));
        timeDAO.excluir(id);
        response.sendRedirect("times?action=list");
    }

    private void relatorioCompleto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        List<String[]> relatorio = timeDAO.listarComInformacoesCompletas();
        request.setAttribute("relatorio", relatorio);
        request.getRequestDispatcher("/WEB-INF/views/time/relatorio.jsp").forward(request, response);
    }
}
