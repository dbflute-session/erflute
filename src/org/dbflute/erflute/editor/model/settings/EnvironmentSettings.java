package org.dbflute.erflute.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentSettings implements Serializable, Cloneable {

    private static final long serialVersionUID = 4937234635221817893L;

    private List<Environment> environments;
    private Environment currentEnvironment;

    public EnvironmentSettings() {
        this.environments = new ArrayList<Environment>();
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    /**
     * environments ��ݒ肵�܂�.
     * 
     * @param environments
     *            environments
     */
    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    /**
     * currentEnvironment ���擾���܂�.
     * 
     * @return currentEnvironment
     */
    public Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * currentEnvironment ��ݒ肵�܂�.
     * 
     * @param currentEnvironment
     *            currentEnvironment
     */
    public void setCurrentEnvironment(Environment currentEnvironment) {
        this.currentEnvironment = currentEnvironment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {
        try {
            final EnvironmentSettings setting = (EnvironmentSettings) super.clone();

            setting.environments = new ArrayList<Environment>();

            final Map<Environment, Environment> oldNewMap = new HashMap<Environment, Environment>();

            for (final Environment environment : this.environments) {
                final Environment newEnvironment = environment.clone();
                setting.environments.add(newEnvironment);
                oldNewMap.put(environment, newEnvironment);
            }

            setting.currentEnvironment = oldNewMap.get(this.currentEnvironment);

            return setting;

        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

}
