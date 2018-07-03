require 'spec_helper'

describe "XML Transformation" do

  context "against schema 4" do
    let!(:template) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XSLT(File.read('/home/app/src/main/webapp/xsl/kernel4_to_oaidc.xsl')) }}

    describe "Good XML" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.5072.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23650.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.to raise_error
        end
    end
    describe "Bad XML remove all namespacing" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23663.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end
  end

end
