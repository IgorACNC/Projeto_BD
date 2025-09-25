package servlets;

import dao.EstadioDAO;
import model.Estadio;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EstadioServlet extends HttpServlet {
    private EstadioDAO estadioDAO = new EstadioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Estadio> estadios = estadioDAO.listar();
                request.setAttribute("estadios", estadios);
                request.getRequestDispatcher("/WEB-INF/views/estadio/listar.jsp").forward(request, response);
            } else if (action.equals("form")) {
                request.getRequestDispatcher("/WEB-INF/views/estadio/formulario.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Estadio estadio = estadioDAO.buscarPorId(id);
                request.setAttribute("estadio", estadio);
                request.getRequestDispatcher("/WEB-INF/views/estadio/editar.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                estadioDAO.excluir(id);
                response.sendRedirect("estadios?action=list");
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
                Estadio estadio = new Estadio();
                estadio.setNome(request.getParameter("nome"));
                estadio.setCapacidade(Integer.parseInt(request.getParameter("capacidade")));
                estadio.setRua(request.getParameter("rua"));
                estadio.setNumero(Integer.parseInt(request.getParameter("numero")));
                estadio.setBairro(request.getParameter("bairro"));
                estadioDAO.inserir(estadio);
                response.sendRedirect("estadios?action=list");
            } else if (action.equals("update")) {
                Estadio estadio = new Estadio();
                estadio.setId_estadio(Integer.parseInt(request.getParameter("id")));
                estadio.setNome(request.getParameter("nome"));
                estadio.setCapacidade(Integer.parseInt(request.getParameter("capacidade")));
                estadio.setRua(request.getParameter("rua"));
                estadio.setNumero(Integer.parseInt(request.getParameter("numero")));
                estadio.setBairro(request.getParameter("bairro"));
                estadioDAO.atualizar(estadio);
                response.sendRedirect("estadios?action=list");
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }
}
