package servlets;

import dao.TecnicoDAO;
import model.Tecnico;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TecnicoServlet extends HttpServlet {
    private TecnicoDAO tecnicoDAO = new TecnicoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Tecnico> tecnicos = tecnicoDAO.listar();
                request.setAttribute("tecnicos", tecnicos);
                request.getRequestDispatcher("/WEB-INF/views/tecnico/listar.jsp").forward(request, response);
            } else if (action.equals("form")) {
                request.getRequestDispatcher("/WEB-INF/views/tecnico/formulario.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Tecnico tecnico = tecnicoDAO.buscarPorId(id);
                request.setAttribute("tecnico", tecnico);
                request.getRequestDispatcher("/WEB-INF/views/tecnico/editar.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                tecnicoDAO.excluir(id);
                response.sendRedirect("tecnicos?action=list");
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
                Tecnico tecnico = new Tecnico();
                tecnico.setNome(request.getParameter("nome"));
                tecnico.setIdade(Integer.parseInt(request.getParameter("idade")));
                tecnico.setQuant_time_treinou(Integer.parseInt(request.getParameter("quant_time_treinou")));
                tecnicoDAO.inserir(tecnico);
                response.sendRedirect("tecnicos?action=list");
            } else if (action.equals("update")) {
                Tecnico tecnico = new Tecnico();
                tecnico.setId_tecnico(Integer.parseInt(request.getParameter("id")));
                tecnico.setNome(request.getParameter("nome"));
                tecnico.setIdade(Integer.parseInt(request.getParameter("idade")));
                tecnico.setQuant_time_treinou(Integer.parseInt(request.getParameter("quant_time_treinou")));
                tecnicoDAO.atualizar(tecnico);
                response.sendRedirect("tecnicos?action=list");
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }
}
