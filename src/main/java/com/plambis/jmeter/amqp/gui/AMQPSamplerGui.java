package com.plambis.jmeter.amqp.gui;

import com.plambis.jmeter.amqp.AMQPSampler;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public abstract class AMQPSamplerGui extends AbstractSamplerGui {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AMQPSamplerGui.class);

    protected JLabeledTextField queue = new JLabeledTextField("Queue");
    protected JLabeledTextField routingKey = new JLabeledTextField("Routing Key");
    protected JLabeledTextField messageTTL = new JLabeledTextField("Message TTL");
    protected JLabeledTextField messageExpires = new JLabeledTextField("Expires");
    private final JCheckBox queueDurable = new JCheckBox("Durable?", true);
    private final JCheckBox queueRedeclare = new JCheckBox("Redeclare?", AMQPSampler.DEFAULT_QUEUE_REDECLARE);
    private final JCheckBox queueExclusive = new JCheckBox("Exclusive", true);
    private final JCheckBox queueAutoDelete = new JCheckBox("Auto Delete?", true);
    private ArgumentsPanel queueArguments = new ArgumentsPanel("Queue Arguments");

    protected JLabeledTextField exchange = new JLabeledTextField("Exchange");
    protected JLabeledChoice exchangeType = new JLabeledChoice("Exchange Type", new String[]{ "direct", "topic", "headers", "fanout"});
    private final JCheckBox exchangeDurable = new JCheckBox("Durable?", AMQPSampler.DEFAULT_EXCHANGE_DURABLE);
    private final JCheckBox exchangeAutoDelete = new JCheckBox("Auto Delete?", AMQPSampler.DEFAULT_EXCHANGE_AUTO_DELETE);
    private final JCheckBox exchangeRedeclare = new JCheckBox("Redeclare?", AMQPSampler.DEFAULT_EXCHANGE_REDECLARE);
    private ArgumentsPanel exchangeArguments = new ArgumentsPanel("Exchange Arguments");

    protected JLabeledTextField virtualHost = new JLabeledTextField("Virtual Host");
    protected JLabeledTextField host = new JLabeledTextField("Host");
    protected JLabeledTextField port = new JLabeledTextField("Port");
    protected JLabeledTextField timeout = new JLabeledTextField("Timeout");
    protected JLabeledTextField username = new JLabeledTextField("Username");
    protected JLabeledTextField password = new JLabeledTextField("Password");
    private final JCheckBox SSL = new JCheckBox("SSL?", false);

    private final JLabeledTextField iterations = new JLabeledTextField("Number of samples to Aggregate");

    protected abstract void setMainPanel(JPanel panel);

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof AMQPSampler)) return;
        AMQPSampler sampler = (AMQPSampler) element;

        exchange.setText(sampler.getExchange());
        exchangeType.setText(sampler.getExchangeType());
        exchangeDurable.setSelected(sampler.getExchangeDurable());
        exchangeAutoDelete.setSelected(sampler.getExchangeAutoDelete());
        exchangeRedeclare.setSelected(sampler.getExchangeRedeclare());
        configureExchangeArguments(sampler);

        queue.setText(sampler.getQueue());
        routingKey.setText(sampler.getRoutingKey());
        messageTTL.setText(sampler.getMessageTTL());
        messageExpires.setText(sampler.getMessageExpires());
        queueDurable.setSelected(sampler.queueDurable());
        queueExclusive.setSelected(sampler.queueExclusive());
        queueAutoDelete.setSelected(sampler.queueAutoDelete());
        queueRedeclare.setSelected(sampler.getQueueRedeclare());
        configureQueueArguments(sampler);

        timeout.setText(sampler.getTimeout());
        iterations.setText(sampler.getIterations());

        host.setText(sampler.getHost());
        port.setText(sampler.getPort());
        virtualHost.setText(sampler.getVirtualHost());
        username.setText(sampler.getUsername());
        password.setText(sampler.getPassword());
        SSL.setSelected(sampler.connectionSSL());
        log.info("AMQPSamplerGui.configure() called");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        exchange.setText("jmeterExchange");
        exchangeType.setText("direct");
        exchangeDurable.setSelected(AMQPSampler.DEFAULT_EXCHANGE_DURABLE);
        exchangeAutoDelete.setSelected(AMQPSampler.DEFAULT_EXCHANGE_AUTO_DELETE);
        exchangeRedeclare.setSelected(AMQPSampler.DEFAULT_EXCHANGE_REDECLARE);
        exchangeArguments.clearGui();

        messageTTL.setText("");
        messageExpires.setText("");
        queue.setText("jmeterQueue");
        routingKey.setText("jmeterRoutingKey");
        queueDurable.setSelected(true);
        queueExclusive.setSelected(false);
        queueAutoDelete.setSelected(false);
        queueRedeclare.setSelected(AMQPSampler.DEFAULT_QUEUE_REDECLARE);
        queueArguments.clearGui();

        iterations.setText(AMQPSampler.DEFAULT_ITERATIONS_STRING);

        virtualHost.setText("/");
        host.setText("localhost");
        port.setText(AMQPSampler.DEFAULT_PORT_STRING);
        username.setText("guest");
        password.setText("guest");
        SSL.setSelected(false);
        timeout.setText(AMQPSampler.DEFAULT_TIMEOUT_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement element) {
        AMQPSampler sampler = (AMQPSampler) element;
        sampler.clear();
        configureTestElement(sampler);

        sampler.setExchange(exchange.getText());
        sampler.setExchangeType(exchangeType.getText());
        sampler.setExchangeDurable(exchangeDurable.isSelected());
        sampler.setExchangeAutoDelete(exchangeAutoDelete.isSelected());
        sampler.setExchangeRedeclare(exchangeRedeclare.isSelected());
        sampler.setExchangeArguments((Arguments) exchangeArguments.createTestElement());

        sampler.setQueue(queue.getText());
        sampler.setRoutingKey(routingKey.getText());
        sampler.setQueueDurable(queueDurable.isSelected());
        sampler.setQueueExclusive(queueExclusive.isSelected());
        sampler.setQueueAutoDelete(queueAutoDelete.isSelected());
        sampler.setQueueRedeclare(queueRedeclare.isSelected());
        sampler.setMessageTTL(messageTTL.getText());
        sampler.setMessageExpires(messageExpires.getText());
        sampler.setQueueArguments((Arguments) queueArguments.createTestElement());

        sampler.setTimeout(timeout.getText());
        sampler.setIterations(iterations.getText());

        sampler.setHost(host.getText());
        sampler.setPort(port.getText());
        sampler.setVirtualHost(virtualHost.getText());
        sampler.setUsername(username.getText());
        sampler.setPassword(password.getText());
        sampler.setConnectionSSL(SSL.isSelected());
        log.info("AMQPSamplerGui.modifyTestElement() called, set user/pass to " + username.getText() + "/" + password.getText() + " on sampler " + sampler);
    }

    protected void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title

        JPanel mainPanel = new VerticalPanel();

        mainPanel.add(makeCommonPanel());

        iterations.setPreferredSize(new Dimension(50,25));
        mainPanel.add(iterations);

        add(mainPanel);

        setMainPanel(mainPanel);
    }

    private Component makeCommonPanel() {
        GridBagConstraints gridBagConstraints, gridBagConstraintsCommon;

        gridBagConstraintsCommon = new GridBagConstraints();
        gridBagConstraintsCommon.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraintsCommon.anchor = GridBagConstraints.WEST;
        gridBagConstraintsCommon.weightx = 0.5;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;

        JPanel commonPanel = new JPanel(new GridBagLayout());

        JPanel exchangeSettings = new JPanel(new GridBagLayout());
        exchangeSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Exchange"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        exchangeSettings.add(exchange, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        exchangeSettings.add(exchangeType, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        exchangeSettings.add(exchangeDurable, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        exchangeSettings.add(exchangeAutoDelete, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        exchangeSettings.add(exchangeRedeclare, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        exchangeSettings.add(exchangeArguments, gridBagConstraints);

        JPanel queueSettings = new JPanel(new GridBagLayout());
        queueSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Queue"));


        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill=GridBagConstraints.NONE;
        queueSettings.add(queue, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        queueSettings.add(routingKey, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        queueSettings.add(messageTTL, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        queueSettings.add(messageExpires, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        queueSettings.add(queueDurable, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        queueSettings.add(queueExclusive, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        queueSettings.add(queueAutoDelete, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        queueSettings.add(queueRedeclare, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        queueSettings.add(queueArguments, gridBagConstraints);

        gridBagConstraintsCommon.gridx = 0;
        gridBagConstraintsCommon.gridy = 0;

        JPanel exchangeQueueSettings = new VerticalPanel();
        exchangeQueueSettings.add(exchangeSettings);
        exchangeQueueSettings.add(queueSettings);

        commonPanel.add(exchangeQueueSettings, gridBagConstraintsCommon);


        JPanel serverSettings = new JPanel(new GridBagLayout());
        serverSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Connection"));

        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill=GridBagConstraints.NONE;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        serverSettings.add(virtualHost, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        serverSettings.add(host, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        serverSettings.add(port, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        serverSettings.add(SSL, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        serverSettings.add(username, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        serverSettings.add(password, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        serverSettings.add(timeout, gridBagConstraints);

        gridBagConstraintsCommon.gridx = 1;
        gridBagConstraintsCommon.gridy = 0;

        commonPanel.add(serverSettings, gridBagConstraintsCommon);

        return commonPanel;
    }

    private void configureQueueArguments(AMQPSampler sampler) {
        Arguments samplerQueueArguments = sampler.getQueueArguments();
        if (samplerQueueArguments != null) {
            queueArguments.configure(samplerQueueArguments);
        } else {
            queueArguments.clearGui();
        }
    }

    private void configureExchangeArguments(AMQPSampler sampler) {
        Arguments samplerExchangeArguments = sampler.getExchangeArguments();
        if (samplerExchangeArguments != null) {
            queueArguments.configure(samplerExchangeArguments);
        } else {
            queueArguments.clearGui();
        }
    }

}
