FROM mediawiki:latest

# Install Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Template Styles
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/TemplateStyles /var/www/html/extensions/TemplateStyles
RUN cd /var/www/html/extensions/TemplateStyles && composer install --no-dev

# JsonConfig
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/JsonConfig /var/www/html/extensions/JsonConfig

# MobileFrontend
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/MobileFrontend /var/www/html/extensions/MobileFrontend