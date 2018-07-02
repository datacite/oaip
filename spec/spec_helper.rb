require 'bundler/setup'
Bundler.setup

require 'maremma'
require 'rspec'
require 'nokogiri'

RSpec.configure do |config|
  config.order = :random
  # config.include Rack::Test::Methods
  config.expect_with :rspec do |c|
    c.syntax = :expect
  end

  config.before do
    ARGV.replace []
  end
end


