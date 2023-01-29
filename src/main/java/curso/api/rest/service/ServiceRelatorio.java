package curso.api.rest.service;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Cache.Connection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class ServiceRelatorio implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public byte[] geraRelatorio(String nomeRelatorio, ServletContext servletContext) throws Exception {

		/* Obter a cconnectiononexão com o banco de dados */
		Connection connection = (Connection) jdbcTemplate.getDataSource().getConnection();

		/* Carregar o caminho do arquivo Jasper */

		String caminhoJasper = servletContext.getRealPath("relatorios") + File.separator + nomeRelatorio + ".jasper";

		/* Gerar o relatorio com os dados e conexão */
		JasperPrint print = JasperFillManager.fillReport(caminhoJasper, new HashMap(),
				(java.sql.Connection) connection);

		/* Exporta para byte o PDF para fazer o download */

		return JasperExportManager.exportReportToPdf(print);

	}
}
