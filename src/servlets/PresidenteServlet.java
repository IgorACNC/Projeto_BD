package servlets;

import dao.PresidenteDAO;
import model.Presidente;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PresidenteServlet extends HttpServlet {
    private PresidenteDAO presidenteDAO = new PresidenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Presidente> presidentes = presidenteDAO.listar();
                request.setAttribute("presidentes", presidentes);
                request.getRequestDispatcher("/WEB-INF/views/presidente/listar.jsp").forward(request, response);
            } else if (action.equals("form")) {
                request.getRequestDispatcher("/WEB-INF/views/presidente/formulario.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Presidente presidente = presidenteDAO.buscarPorId(id);
                request.setAttribute("presidente", presidente);
                request.getRequestDispatcher("/WEB-INF/views/presidente/editar.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                presidenteDAO.excluir(id);
                response.sendRedirect("presidentes?action=list");
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
                Presidente presidente = new Presidente();
                presidente.setNome(request.getParameter("nome"));
                presidente.setIdade(Integer.parseInt(request.getParameter("idade")));
                presidente.setQuant_titulo(Integer.parseInt(request.getParameter("quant_titulo")));
                presidente.setTempo_cargo(Integer.parseInt(request.getParameter("tempo_cargo")));
                presidenteDAO.inserir(presidente);
                response.sendRedirect("presidentes?action=list");
            } else if (action.equals("update")) {
                Presidente presidente = new Presidente();
                presidente.setId_presidente(Integer.parseInt(request.getParameter("id")));
                presidente.setNome(request.getParameter("nome"));
                presidente.setIdade(Integer.parseInt(request.getParameter("idade")));
                presidente.setQuant_titulo(Integer.parseInt(request.getParameter("quant_titulo")));
                presidente.setTempo_cargo(Integer.parseInt(request.getParameter("tempo_cargo")));
                presidenteDAO.atualizar(presidente);
                response.sendRedirect("presidentes?action=list");
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }
}
