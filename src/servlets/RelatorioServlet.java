package servlets;

import dao.RelatorioDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RelatorioServlet extends HttpServlet {
    private RelatorioDAO relatorioDAO = new RelatorioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("menu")) {
                request.getRequestDispatcher("/WEB-INF/views/relatorio/menu.jsp").forward(request, response);
            } else if (action.equals("times")) {
                List<String[]> relatorio = relatorioDAO.relatorioTimesComJogadores();
                request.setAttribute("relatorio", relatorio);
                request.setAttribute("titulo", "Times com Estatísticas de Jogadores");
                request.getRequestDispatcher("/WEB-INF/views/relatorio/exibir.jsp").forward(request, response);
            } else if (action.equals("posicoes")) {
                List<String[]> relatorio = relatorioDAO.relatorioEstatisticasPorPosicao();
                request.setAttribute("relatorio", relatorio);
                request.setAttribute("titulo", "Estatísticas por Posição");
                request.getRequestDispatcher("/WEB-INF/views/relatorio/exibir.jsp").forward(request, response);
            } else if (action.equals("estadios")) {
                List<String[]> relatorio = relatorioDAO.relatorioTimesComEstadios();
                request.setAttribute("relatorio", relatorio);
                request.setAttribute("titulo", "Times e seus Estádios");
                request.getRequestDispatcher("/WEB-INF/views/relatorio/exibir.jsp").forward(request, response);
            } else if (action.equals("tecnicos")) {
                List<String[]> relatorio = relatorioDAO.relatorioTecnicosExperiencia();
                request.setAttribute("relatorio", relatorio);
                request.setAttribute("titulo", "Técnicos por Experiência");
                request.getRequestDispatcher("/WEB-INF/views/relatorio/exibir.jsp").forward(request, response);
            } else if (action.equals("nacionalidades")) {
                List<String[]> relatorio = relatorioDAO.relatorioJogadoresPorNacionalidade();
                request.setAttribute("relatorio", relatorio);
                request.setAttribute("titulo", "Jogadores por Nacionalidade");
                request.getRequestDispatcher("/WEB-INF/views/relatorio/exibir.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }
}
